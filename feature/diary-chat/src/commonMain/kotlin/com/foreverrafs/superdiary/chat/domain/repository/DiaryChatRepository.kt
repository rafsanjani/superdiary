package com.foreverrafs.superdiary.chat.domain.repository

import com.foreverrafs.superdiary.ai.domain.model.DiaryChatMessage
import com.foreverrafs.superdiary.domain.model.Diary
import kotlinx.coroutines.flow.Flow

interface DiaryChatRepository {
    fun getAllDiaries(): Flow<List<Diary>>

    fun getChatMessages(): Flow<List<DiaryChatMessage>>

    suspend fun queryDiaries(messages: List<DiaryChatMessage>): String?

    suspend fun saveChatMessage(message: DiaryChatMessage)
}
