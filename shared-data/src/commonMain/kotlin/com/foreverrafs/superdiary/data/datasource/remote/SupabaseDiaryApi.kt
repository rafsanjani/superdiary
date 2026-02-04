package com.foreverrafs.superdiary.data.datasource.remote

import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.data.model.DiaryDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Count
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.realtime.selectAsFlow
import kotlin.time.Clock
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.take

class SupabaseDiaryApi(
    private val supabase: SupabaseClient,
) : DiaryApi {
    @OptIn(SupabaseExperimental::class)
    override fun fetchAll(): Flow<List<DiaryDto>> =
        supabase.from(TABLE_NAME).selectAsFlow(DiaryDto::id)

    override suspend fun delete(diary: DiaryDto): Result<Boolean> = try {
        val deleted = diary.copy(
            isDeleted = true,
            updatedAt = Clock.System.now().toEpochMilliseconds(),
        )
        supabase.from(TABLE_NAME).update(deleted) {
            filter {
                DiaryDto::id eq diary.id
            }
        }
        Result.Success(true)
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        Result.Failure(e)
    }

    override suspend fun save(diary: DiaryDto): Result<Boolean> = try {
        supabase.from(TABLE_NAME).upsert(diary)
        Result.Success(true)
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        Result.Failure(e)
    }

    override suspend fun fetch(count: Int): Result<List<DiaryDto>> = try {
        val data: List<DiaryDto> = supabase.from(TABLE_NAME).select {
            filter {
                DiaryDto::isDeleted eq false
            }
            limit(count = count.toLong())
            order(column = "date", order = Order.DESCENDING)
        }.decodeList()

        Result.Success(data)
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        Result.Failure(e)
    }

    override suspend fun countItems(): Result<Long> = try {
        val count = supabase
            .from(TABLE_NAME)
            .select {
                count(count = Count.EXACT)
                filter {
                    DiaryDto::isDeleted eq false
                }
            }
            .countOrNull()
        Result.Success(count ?: 0L)
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        Result.Failure(e)
    }

    companion object {
        private const val TABLE_NAME = "diary"
    }
}
