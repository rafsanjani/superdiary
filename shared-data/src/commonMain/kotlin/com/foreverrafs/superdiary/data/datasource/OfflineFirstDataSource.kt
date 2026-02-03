package com.foreverrafs.superdiary.data.datasource

import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.data.datasource.remote.DiaryApi
import com.foreverrafs.superdiary.data.mapper.toDiary as toDomainDiary
import com.foreverrafs.superdiary.data.model.toDiary as toDomainDiaryFromDto
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.model.WeeklySummary
import com.foreverrafs.superdiary.domain.model.toDatabase
import com.foreverrafs.superdiary.domain.model.toDto
import com.foreverrafs.superdiary.domain.repository.DataSource
import kotlin.concurrent.Volatile
import kotlin.time.Clock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface Syncable {
    fun isSyncing(): Boolean
}

class OfflineFirstDataSource(
    private val local: LocalDataSource,
    private val api: DiaryApi,
    private val logger: AggregateLogger,
    private val clock: Clock,
) : DataSource, Syncable {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val syncMutex = Mutex()

    @Volatile
    private var syncStarted = false

    override suspend fun save(diary: Diary): Long {
        val result = local.save(diary)
        scheduleSync()
        return result
    }

    init {
        ensureSyncStarted()
    }

    override fun isSyncing(): Boolean = syncStarted

    override suspend fun save(diaries: List<Diary>): Long {
        val result = local.save(diaries)
        scheduleSync()
        return result
    }

    override suspend fun update(diary: Diary): Int {
        val result = local.update(diary)
        scheduleSync()
        return result
    }

    override suspend fun delete(diaries: List<Diary>): Int {
        val result = local.delete(diaries)
        scheduleSync()
        return result
    }

    override fun fetchAll(): Flow<List<Diary>> {
        ensureSyncStarted()
        return local.fetchAll()
    }

    override fun fetchFavorites(): Flow<List<Diary>> {
        ensureSyncStarted()
        return local.fetchFavorites()
    }

    override fun find(entry: String): Flow<List<Diary>> {
        ensureSyncStarted()
        return local.find(entry)
    }

    override fun findByDate(date: kotlin.time.Instant): Flow<List<Diary>> {
        ensureSyncStarted()
        return local.findByDate(date)
    }

    override fun find(from: kotlin.time.Instant, to: kotlin.time.Instant): Flow<List<Diary>> {
        ensureSyncStarted()
        return local.find(from, to)
    }

    override fun find(id: Long): Diary? = local.find(id)

    override suspend fun deleteAll() = local.deleteAll()

    override fun getLatest(count: Int): Flow<List<Diary>> {
        ensureSyncStarted()
        return local.getLatest(count)
    }

    override suspend fun count(): Long = local.count()

    override suspend fun save(summary: WeeklySummary) = local.save(summary)

    override fun getOne(): WeeklySummary? = local.getOne()

    override suspend fun clearChatMessages() = local.clearChatMessages()

    private fun ensureSyncStarted() {
        if (syncStarted) {
            logger.i(TAG) {
                "Sync job is already running"
            }
            return
        }
        syncStarted = true

        logger.i(TAG) {
            "Syncing remote and local entries"
        }

        scope.launch {
            scheduleSync()
        }

        scope.launch {
            api.fetchAll()
                .catch { e ->
                    logger.e(tag = TAG, throwable = e) { "Realtime sync failed" }
                }
                .collect { remoteDiaries ->
                    applyRemoteUpdates(remoteDiaries)
                }
        }
    }

    private suspend fun scheduleSync() {
        syncMutex.withLock {
            pushPendingDeletes()
            pushPendingUpserts()
        }
    }

    private suspend fun pushPendingDeletes() {
        val pendingDeletes = local.database.getPendingDeleteDiaries()
        pendingDeletes.forEach { diary ->
            val result = api.delete(diary.toDomainDiary().toDto())
            if (result is Result.Success) {
                diary.id?.let { id ->
                    local.database.deleteDiaries(listOf(id))
                }
            } else if (result is Result.Failure) {
                logger.e(tag = TAG, throwable = result.error) { "Failed to sync deletion" }
            }
        }
    }

    private suspend fun pushPendingUpserts() {
        val pendingSync = local.database.getPendingSyncDiaries()
        pendingSync.forEach { diary ->
            val payload =
                diary.toDomainDiary().copy(updatedAt = clock.now(), isSynced = false).toDto()
            val result = api.save(payload)
            if (result is Result.Success) {
                diary.id?.let { id ->
                    local.database.markDiarySynced(id)
                }
            } else if (result is Result.Failure) {
                logger.e(tag = TAG, throwable = result.error) { "Failed to sync diary" }
            }
        }
    }

    private suspend fun applyRemoteUpdates(remoteDiaries: List<com.foreverrafs.superdiary.data.model.DiaryDto>) {
        remoteDiaries.forEach { remoteDto ->
            val remoteDiary = remoteDto.toDomainDiaryFromDto()
            val localDiary = remoteDiary.id?.let { id ->
                local.database.findByIdIncludingDeleted(id)?.toDomainDiary()
            }

            if (remoteDto.isDeleted) {
                if (shouldApplyRemote(remoteDiary, localDiary)) {
                    remoteDiary.id?.let { id ->
                        local.database.deleteDiaries(listOf(id))
                    }
                }
                return@forEach
            }

            if (shouldApplyRemote(remoteDiary, localDiary)) {
                local.database.upsert(
                    remoteDiary.copy(isSynced = true, isMarkedForDelete = false).toDatabase(),
                )
            }
        }
    }

    private fun shouldApplyRemote(remote: Diary, local: Diary?): Boolean {
        if (local == null) return true
        if (remote.updatedAt > local.updatedAt) return true
        if (remote.isMarkedForDelete && remote.updatedAt >= local.updatedAt) return true
        return false
    }

    companion object {
        private const val TAG = "OfflineFirstDataSource"
    }
}
