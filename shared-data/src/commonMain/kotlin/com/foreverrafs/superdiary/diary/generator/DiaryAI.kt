package com.foreverrafs.superdiary.diary.generator

import com.foreverrafs.superdiary.diary.model.Diary
import kotlinx.coroutines.flow.Flow

interface DiaryAI {
    fun generateDiary(prompt: String, wordCount: Int): Flow<String>
    suspend fun generateWeeklySummary(diaries: List<Diary>): String
}
