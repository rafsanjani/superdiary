package com.foreverrafs.superdiary.data.datasource.remote

import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.data.model.DiaryDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Count
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow

class SupabaseDiaryApi(
    private val supabase: SupabaseClient,
) : DiaryApi {
    @OptIn(SupabaseExperimental::class)
    override fun fetchAll(): Flow<List<DiaryDto>> = supabase.from(TABLE_NAME).selectAsFlow(DiaryDto::id)

    override suspend fun delete(diary: DiaryDto): Result<Boolean> = try {
        supabase.from(TABLE_NAME).delete {
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
        supabase.from(TABLE_NAME).insert(diary)
        Result.Success(true)
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        Result.Failure(e)
    }

    override suspend fun fetch(count: Int): Result<List<DiaryDto>> {
        return try {
            val data: List<DiaryDto> = supabase.from(TABLE_NAME).select {
                limit(count = count.toLong())
                order(column = "date", order = Order.DESCENDING)
            }.decodeList()

            Result.Success(data)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.Failure(e)
        }
    }

    override suspend fun countItems(): Result<Long> {
        return try {
            val count = supabase
                .from(TABLE_NAME)
                .select {
                    count(count = Count.EXACT)
                }
                .countOrNull()
            Result.Success(count ?: 0L)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.Failure(e)
        }
    }

    companion object {
        private const val TABLE_NAME = "diary"
    }
}
