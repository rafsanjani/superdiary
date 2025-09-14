package com.foreverrafs.superdiary.ai.api

import com.foreverrafs.superdiary.ai.domain.model.DiaryChatMessage
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.model.WeeklySummary
import kotlinx.coroutines.flow.Flow

interface DiaryAI {
    /** Generates a diary entry of [wordCount] */
    fun generateDiary(prompt: String, wordCount: Int): Flow<String>

    /** Generates a summary from a list of diary entries. */
    fun generateSummary(
        diaries: List<Diary>,
        onCompletion: suspend (summary: WeeklySummary) -> Unit,
    ): Flow<String>

    suspend fun queryDiaries(
        messages: List<DiaryChatMessage>,
    ): String?
}
