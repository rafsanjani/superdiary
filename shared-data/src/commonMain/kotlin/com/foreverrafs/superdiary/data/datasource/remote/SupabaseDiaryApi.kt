package com.foreverrafs.superdiary.data.datasource.remote

import com.foreverrafs.superdiary.data.model.DiaryDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.flow.Flow

class SupabaseDiaryApi(
    private val supabase: SupabaseClient,
) : DiaryApi {
    @OptIn(SupabaseExperimental::class)
    override fun fetchAll(): Flow<List<DiaryDto>> = supabase.from(TABLE_NAME)
        .selectAsFlow(DiaryDto::id)

    companion object {
        private const val TABLE_NAME = "diary"
    }
}
