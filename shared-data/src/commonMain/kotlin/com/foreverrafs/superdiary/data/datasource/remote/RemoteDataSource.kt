package com.foreverrafs.superdiary.data.datasource.remote

import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.model.DiaryDto
import com.foreverrafs.superdiary.data.model.toDiary
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.model.WeeklySummary
import com.foreverrafs.superdiary.domain.model.toDto
import com.foreverrafs.superdiary.domain.repository.DataSource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

@Deprecated("Use DiaryApi instead")
class RemoteDataSource(
    private val supabase: SupabaseClient,
    private val logger: AggregateLogger,
) : DataSource {

    private var dataCache: List<DiaryDto>? = null

    override suspend fun add(diary: Diary): Long {
        val dto = diary.toDto()

        return try {
            supabase.from(TABLE_NAME)
                .insert(dto)
            1
        } catch (e: RestException) {
            logger.e(tag = TAG, throwable = e) {
                "Remote Diary saved"
            }
            0
        }
    }

    override suspend fun getPendingDeletes(): List<Diary> {
        TODO("Not yet implemented")
    }

    override suspend fun getPendingSyncs(): List<Diary> {
        TODO("Not yet implemented")
    }
    override suspend fun delete(diaries: List<Diary>): Int {
        supabase.from(TABLE_NAME)
            .delete {
                filter {
                    "id" in (diaries.map { it.id.toString() })
                }
            }

        return 1
    }

    override suspend fun update(diary: Diary): Int {
        TODO("Not yet implemented")
    }

    @OptIn(SupabaseExperimental::class)
    override fun fetchAll(): Flow<List<Diary>> = supabase.from(TABLE_NAME)
        .selectAsFlow(DiaryDto::id)
        .onStart {
            dataCache?.let { emit(it) }
        }
        .catch { emit(dataCache ?: emptyList()) }
        .map {
            // cache it
            dataCache = it

            // transform and send to chain
            it.map { dto -> dto.toDiary() }
        }

    override fun fetchFavorites(): Flow<List<Diary>> {
        TODO("Not yet implemented")
    }

    override fun find(entry: String): Flow<List<Diary>> {
        TODO("Not yet implemented")
    }

    override fun find(from: kotlin.time.Instant, to: kotlin.time.Instant): Flow<List<Diary>> {
        TODO("Not yet implemented")
    }

    override fun find(id: Long): Diary? = dataCache?.firstOrNull { it.id == id }?.toDiary()

    override fun findByDate(date: kotlin.time.Instant): Flow<List<Diary>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAll() {
        TODO("Not yet implemented")
    }

    override fun getLatestEntries(count: Int): Flow<List<Diary>> {
        TODO("Not yet implemented")
    }

    override suspend fun countEntries(): Long {
        TODO("Not yet implemented")
    }

    override suspend fun insertWeeklySummary(summary: WeeklySummary) {
        TODO("Not yet implemented")
    }

    override fun getWeeklySummary(): WeeklySummary? {
        TODO("Not yet implemented")
    }

    override suspend fun clearChatMessages() {
        TODO("Not yet implemented")
    }

    override suspend fun addAll(diaries: List<Diary>): Long {
        TODO("Not yet implemented")
    }
    companion object {
        private const val TABLE_NAME = "diary"
        private const val TAG = "RemoteDataSource"
    }
}
