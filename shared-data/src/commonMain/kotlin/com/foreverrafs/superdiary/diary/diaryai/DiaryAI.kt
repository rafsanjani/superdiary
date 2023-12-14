package com.foreverrafs.superdiary.diary.diaryai

import com.foreverrafs.superdiary.diary.model.Diary
import kotlinx.coroutines.flow.Flow

interface DiaryAI {
    fun generateDiary(prompt: String, wordCount: Int): Flow<String>
    suspend fun generateWeeklySummary(diaries: List<Diary>): String
    fun generateWeeklySummaryAsync(diaries: List<Diary>): Flow<String>
    suspend fun queryDiaries(diaries: List<Diary>, query: String): String
}
