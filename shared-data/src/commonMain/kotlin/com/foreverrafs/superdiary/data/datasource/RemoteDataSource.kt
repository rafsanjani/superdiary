package com.foreverrafs.superdiary.data.datasource

import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.diaryai.DiaryChatMessage
import com.foreverrafs.superdiary.data.model.DiaryDto
import com.foreverrafs.superdiary.data.model.toDiary
import com.foreverrafs.superdiary.data.model.toDto
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.model.WeeklySummary
import com.foreverrafs.superdiary.domain.repository.DataSource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.datetime.Instant

class RemoteDataSource(
    private val supabase: SupabaseClient,
    private val logger: AggregateLogger,
) : DataSource {

    private var dataCache: List<DiaryDto>? = null

    override suspend fun add(diary: Diary): Long {
        val dto = diary.toDto()

        supabase.from(TABLE_NAME)
            .insert(dto)

        logger.d(TAG) {
            "Remote Diary saved"
        }
        return 1
    }

    override suspend fun delete(diary: Diary): Int {
        TODO("Not yet implemented")
    }

    override suspend fun delete(diaries: List<Diary>): Int {
        TODO("Not yet implemented")
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

    override fun find(from: Instant, to: Instant): Flow<List<Diary>> {
        TODO("Not yet implemented")
    }

    override fun find(id: Long): Flow<Diary?> = flow {
        val data = dataCache?.firstOrNull { it.id == id }
        emit(data?.toDiary())
    }

    override fun findByDate(date: Instant): Flow<List<Diary>> {
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

    override suspend fun saveChatMessage(message: DiaryChatMessage) {
        TODO("Not yet implemented")
    }

    override suspend fun clearChatMessages() {
        TODO("Not yet implemented")
    }

    override fun getChatMessages(): Flow<List<DiaryChatMessage>> {
        TODO("Not yet implemented")
    }

    companion object {
        private const val TABLE_NAME = "diary"
        private const val TAG = "RemoteDataSource"
    }
}
