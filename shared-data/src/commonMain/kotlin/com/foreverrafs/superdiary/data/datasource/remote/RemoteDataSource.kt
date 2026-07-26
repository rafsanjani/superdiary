package com.foreverrafs.superdiary.data.datasource.remote

import androidx.paging.PagingData
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.model.DiaryDto
import com.foreverrafs.superdiary.data.model.toDiary
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.model.WeeklySummary
import com.foreverrafs.superdiary.domain.model.toDto
import com.foreverrafs.superdiary.domain.repository.DataSource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow

@Deprecated("Use DiaryApi instead")
class RemoteDataSource(
    private val supabase: SupabaseClient,
    private val logger: AggregateLogger,
) : DataSource {

    private var dataCache: List<DiaryDto>? = null

    override suspend fun save(diary: Diary): Long {
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

    override fun fetchAllPaged(): Flow<PagingData<Diary>> {
        TODO("Not yet implemented")
    }

    override fun fetchFavoritesPaged(): Flow<PagingData<Diary>> {
        TODO("Not yet implemented")
    }

    override fun findPaged(entry: String): Flow<PagingData<Diary>> {
        TODO("Not yet implemented")
    }

    override fun findPaged(from: kotlin.time.Instant, to: kotlin.time.Instant): Flow<PagingData<Diary>> {
        TODO("Not yet implemented")
    }

    override fun findPaged(
        entry: String,
        from: kotlin.time.Instant,
        to: kotlin.time.Instant,
    ): Flow<PagingData<Diary>> {
        TODO("Not yet implemented")
    }

    override fun find(id: Long): Diary? = dataCache?.firstOrNull { it.id == id }?.toDiary()

    override fun findByDatePaged(date: kotlin.time.Instant): Flow<PagingData<Diary>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAll() {
        TODO("Not yet implemented")
    }

    override fun getLatest(count: Int): Flow<List<Diary>> {
        TODO("Not yet implemented")
    }

    override suspend fun count(): Long {
        TODO("Not yet implemented")
    }

    override suspend fun save(summary: WeeklySummary) {
        TODO("Not yet implemented")
    }

    override fun getOne(): WeeklySummary? {
        TODO("Not yet implemented")
    }

    override suspend fun clearChatMessages() {
        TODO("Not yet implemented")
    }

    override suspend fun save(diaries: List<Diary>): Long {
        TODO("Not yet implemented")
    }

    companion object {
        private const val TABLE_NAME = "diary"
        private const val TAG = "RemoteDataSource"
    }
}
