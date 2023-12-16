package com.foreverrafs.superdiary.diary.diaryai

import com.foreverrafs.superdiary.diary.model.Diary
import kotlinx.coroutines.flow.Flow

interface DiaryAI {
    fun generateDiary(prompt: String, wordCount: Int): Flow<String>
    fun getWeeklySummary(diaries: List<Diary>): Flow<String>
    suspend fun queryDiaries(diaries: List<Diary>, query: String): String
}
