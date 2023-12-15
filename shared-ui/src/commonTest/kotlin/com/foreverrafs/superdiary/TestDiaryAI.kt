package com.foreverrafs.superdiary

import com.foreverrafs.superdiary.diary.diaryai.DiaryAI
import com.foreverrafs.superdiary.diary.model.Diary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class TestDiaryAI : DiaryAI {
    override fun generateDiary(prompt: String, wordCount: Int): Flow<String> =
        flowOf("Test Generated diary")

    override suspend fun generateWeeklySummary(diaries: List<Diary>): String = "Weekly Summary"

    override fun generateWeeklySummaryAsync(diaries: List<Diary>): Flow<String> =
        flowOf("Test Weekly Diary")

    override suspend fun queryDiaries(diaries: List<Diary>, query: String): String =
        "Answer to your question"
}
