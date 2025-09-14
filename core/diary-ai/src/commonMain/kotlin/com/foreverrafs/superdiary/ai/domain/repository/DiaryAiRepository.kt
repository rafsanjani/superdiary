package com.foreverrafs.superdiary.ai.domain.repository

import com.foreverrafs.superdiary.ai.domain.model.DiaryChatMessage
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.model.WeeklySummary
import kotlinx.coroutines.flow.Flow

interface DiaryAiRepository {
    fun generateDiary(prompt: String, wordCount: Int): Flow<String>
    fun generateSummary(diaries: List<Diary>, onCompletion: (WeeklySummary) -> Unit): Flow<String>
    suspend fun saveChatMessage(message: DiaryChatMessage)
    fun getChatMessages(): Flow<List<DiaryChatMessage>>
    suspend fun clearChatMessages()
}
