package com.foreverrafs.superdiary.insights.data

import com.foreverrafs.superdiary.ai.api.DiaryAI
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.repository.DataSource
import com.foreverrafs.superdiary.insights.domain.repository.WritingInsightsRepository
import kotlinx.coroutines.flow.Flow

class WritingInsightsRepositoryImpl(
    private val dataSource: DataSource,
    private val diaryAI: DiaryAI,
) : WritingInsightsRepository {
    override fun observeEntries(): Flow<List<Diary>> =
        dataSource.getLatest(Int.MAX_VALUE)

    override suspend fun generateInsights(diaries: List<Diary>): String? =
        diaryAI.generateWritingInsights(diaries).takeUnless { it.isNullOrBlank() }
}
