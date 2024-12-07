package com.foreverrafs.superdiary.data.diaryai

import com.foreverrafs.superdiary.domain.model.Diary
import kotlinx.coroutines.flow.Flow

interface DiaryAI {
    /** Generates a diary entry of [wordCount] */
    fun generateDiary(prompt: String, wordCount: Int): Flow<String>
    fun generateSummary(diaries: List<Diary>): Flow<String>
    suspend fun queryDiaries(
        messages: List<DiaryChatMessage>,
    ): String
}
