package com.foreverrafs.superdiary.data.diaryai

import com.foreverrafs.superdiary.data.model.Diary
import kotlinx.coroutines.flow.Flow

interface DiaryAI {
    fun generateDiary(prompt: String, wordCount: Int): Flow<String>
    fun getWeeklySummary(diaries: List<Diary>): Flow<String>
    suspend fun queryDiaries(
        messages: List<DiaryChatMessage>,
    ): String
}
