package com.foreverrafs.superdiary.insights.domain.repository

import com.foreverrafs.superdiary.domain.model.Diary
import kotlinx.coroutines.flow.Flow

interface WritingInsightsRepository {
    fun observeEntries(): Flow<List<Diary>>

    suspend fun generateInsights(diaries: List<Diary>): String?
}
