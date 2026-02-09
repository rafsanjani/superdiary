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
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.time.Clock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface Syncable {
    val initialSyncState: StateFlow<InitialSyncState>
}

enum class InitialSyncState {
    NotStarted,
    Syncing,
    Completed,
    Failed,
}

class OfflineFirstDataSource(
    private val database: LocalDataSource,
    private val diaryApi: DiaryApi,
    private val logger: AggregateLogger,
    private val clock: Clock,
) : DataSource, Syncable {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val syncMutex = Mutex()

    @OptIn(ExperimentalAtomicApi::class)
    private val initialSyncCompleted = AtomicBoolean(false)
    private var initialSyncJob: Job? = null
    private val _initialSyncState = MutableStateFlow(InitialSyncState.NotStarted)

    @Volatile
    private var syncStarted = false

    override suspend fun save(diary: Diary): Long {
        val result = database.save(diary)
        scheduleSync()
        return result
    }

    init {
        logger.i(TAG) { "OfflineFirstDataSource init: starting sync bootstrap" }
        ensureSyncStarted()
    }

    override val initialSyncState: StateFlow<InitialSyncState> = _initialSyncState.asStateFlow()

    override suspend fun save(diaries: List<Diary>): Long {
        val result = database.save(diaries)
        scheduleSync()
        return result
    }

    override suspend fun update(diary: Diary): Int {
        val result = database.update(diary)
        scheduleSync()
        return result
    }

    override suspend fun delete(diaries: List<Diary>): Int {
        val result = database.delete(diaries)
        scheduleSync()
        return result
    }

    override fun fetchAll(): Flow<List<Diary>> {
        ensureSyncStarted()
        return database.fetchAll()
    }

    override fun fetchFavorites(): Flow<List<Diary>> {
        ensureSyncStarted()
        return database.fetchFavorites()
    }

    override fun find(entry: String): Flow<List<Diary>> {
        ensureSyncStarted()
        return database.find(entry)
    }

    override fun findByDate(date: kotlin.time.Instant): Flow<List<Diary>> {
        ensureSyncStarted()
        return database.findByDate(date)
    }

    override fun find(from: kotlin.time.Instant, to: kotlin.time.Instant): Flow<List<Diary>> {
        ensureSyncStarted()
        return database.find(from, to)
    }

    override fun find(id: Long): Diary? = database.find(id)

    override suspend fun deleteAll() = database.deleteAll()

    override fun getLatest(count: Int): Flow<List<Diary>> {
        ensureSyncStarted()
        return database.getLatest(count)
    }

    override suspend fun count(): Long = database.count()

    override suspend fun save(summary: WeeklySummary) = database.save(summary)

    override fun getOne(): WeeklySummary? = database.getOne()

    override suspend fun clearChatMessages() = database.clearChatMessages()

    private fun ensureSyncStarted() {
        if (syncStarted) {
            logger.i(TAG) {
                "Sync job is already running"
            }
            return
        }
        syncStarted = true
        _initialSyncState.value = InitialSyncState.Syncing

        logger.i(TAG) {
            "Syncing remote and local entries"
        }

        initialSyncJob = scope.launch {
            logger.i(TAG) { "Initial sync: pushing local pending changes" }
            scheduleSync()
            logger.i(TAG) { "Initial sync: local pending push complete" }
        }

        scope.launch {
            logger.i(TAG) { "Initial sync: subscribing to realtime feed" }
            diaryApi.fetchAll()
                .catch { e ->
                    logger.e(tag = TAG, throwable = e) { "Realtime sync failed" }
                    markInitialSyncFailed()
                }
                .collect { remoteDiaries ->
                    logger.i(TAG) {
                        "Realtime update received: ${remoteDiaries.size} remote entries"
                    }
                    applyRemoteUpdates(remoteDiaries)
                    markInitialSyncCompleteIfNeeded()
                }
        }
    }

    private suspend fun scheduleSync() {
        syncMutex.withLock {
            logger.i(TAG) { "Sync cycle started" }
            pushPendingDeletes()
            pushPendingUpserts()
            logger.i(TAG) { "Sync cycle finished" }
        }
    }

    private suspend fun pushPendingDeletes() {
        val pendingDeletes = database.database.getPendingDeleteDiaries()
        logger.i(TAG) { "Pending deletes: ${pendingDeletes.size}" }
        pendingDeletes.forEach { diary ->
            val result = diaryApi.delete(diary.toDomainDiary().toDto())
            if (result is Result.Success) {
                diary.id?.let { id ->
                    logger.i(TAG) { "Delete synced for diaryId=$id; removing locally" }
                    database.database.deleteDiaries(listOf(id))
                }
            } else if (result is Result.Failure) {
                logger.e(tag = TAG, throwable = result.error) { "Failed to sync deletion" }
            }
        }
    }

    private suspend fun pushPendingUpserts() {
        val pendingSync = database.database.getPendingSyncDiaries()
        logger.i(TAG) { "Pending upserts: ${pendingSync.size}" }
        pendingSync.forEach { diary ->
            val payload =
                diary.toDomainDiary().copy(updatedAt = clock.now(), isSynced = false).toDto()
            val result = diaryApi.save(payload)
            if (result is Result.Success) {
                diary.id?.let { id ->
                    logger.i(TAG) { "Upsert synced for diaryId=$id; marking synced locally" }
                    database.database.markDiarySynced(id)
                }
            } else if (result is Result.Failure) {
                logger.e(tag = TAG, throwable = result.error) { "Failed to sync diary" }
            }
        }
    }

    private suspend fun applyRemoteUpdates(remoteDiaries: List<com.foreverrafs.superdiary.data.model.DiaryDto>) {
        logger.i(TAG) { "Applying remote updates: ${remoteDiaries.size} items" }
        val remoteIds = remoteDiaries.mapNotNull { it.id }.toSet()
        remoteDiaries.forEach { remoteDto ->
            val remoteDiary = remoteDto.toDomainDiaryFromDto()
            val localDiary = remoteDiary.id?.let { id ->
                database.database.findByIdIncludingDeleted(id)?.toDomainDiary()
            }

            if (remoteDto.isDeleted) {
                logger.i(TAG) { "Remote delete for diaryId=${remoteDiary.id}" }
                if (shouldApplyRemote(remoteDiary, localDiary)) {
                    remoteDiary.id?.let { id ->
                        logger.i(TAG) { "Applying remote delete for diaryId=$id" }
                        database.database.deleteDiaries(listOf(id))
                    }
                }
                return@forEach
            }

            if (shouldApplyRemote(remoteDiary, localDiary)) {
                logger.i(TAG) { "Applying remote upsert for diaryId=${remoteDiary.id}" }
                database.database.upsert(
                    remoteDiary.copy(isSynced = true, isMarkedForDelete = false).toDatabase(),
                )
            } else {
                logger.i(TAG) { "Skipping remote update for diaryId=${remoteDiary.id}" }
            }
        }

        // If a row was hard-deleted on the server, it will be missing from the realtime snapshot.
        // Remove locally only if the row is already synced (avoid clobbering local pending edits).
        val localSyncedIds = database.database.getSyncedDiaryIds()
        val missingIds = localSyncedIds.filter { it !in remoteIds }
        if (missingIds.isNotEmpty()) {
            logger.i(TAG) { "Applying remote hard deletes for ids=${missingIds.joinToString(",")}" }
            database.database.deleteDiaries(missingIds)
        }
    }

    private fun shouldApplyRemote(remote: Diary, local: Diary?): Boolean {
        if (local == null) return true
        if (remote.updatedAt > local.updatedAt) return true
        if (remote.isMarkedForDelete && remote.updatedAt >= local.updatedAt) return true
        return false
    }

    @OptIn(ExperimentalAtomicApi::class)
    private suspend fun markInitialSyncCompleteIfNeeded() {
        if (initialSyncCompleted.load()) return
        initialSyncJob?.join()
        if (initialSyncCompleted.compareAndSet(expectedValue = false, newValue = true)) {
            _initialSyncState.value = InitialSyncState.Completed
            logger.i(TAG) { "Initial sync completed" }
        }
    }

    @OptIn(ExperimentalAtomicApi::class)
    private fun markInitialSyncFailed() {
        if (initialSyncCompleted.compareAndSet(expectedValue = false, newValue = true)) {
            _initialSyncState.value = InitialSyncState.Failed
            logger.w(TAG) { "Initial sync failed" }
        }
    }

    companion object {
        private const val TAG = "OfflineFirstDataSource"
    }
}
