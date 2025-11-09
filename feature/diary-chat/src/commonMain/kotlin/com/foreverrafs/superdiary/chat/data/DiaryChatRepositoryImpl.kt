package com.foreverrafs.superdiary.chat.data

import com.foreverrafs.superdiary.ai.api.DiaryAI
import com.foreverrafs.superdiary.ai.domain.model.DiaryChatMessage
import com.foreverrafs.superdiary.ai.domain.model.toDatabase
import com.foreverrafs.superdiary.ai.domain.model.toDiaryChatMessage
import com.foreverrafs.superdiary.chat.domain.repository.DiaryChatRepository
import com.foreverrafs.superdiary.data.mapper.toDiary
import com.foreverrafs.superdiary.database.Database
import com.foreverrafs.superdiary.domain.model.Diary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DiaryChatRepositoryImpl(
    private val database: Database,
    private val diaryAI: DiaryAI,
) : DiaryChatRepository {
    override fun getAllDiaries(): Flow<List<Diary>> = database.getAllDiaries().map {
        it.map { it.toDiary() }
    }

    override suspend fun queryDiaries(messages: List<DiaryChatMessage>): String? = diaryAI.queryDiaries(messages)

    override suspend fun saveChatMessage(message: DiaryChatMessage) {
        database.saveChatMessage(message.toDatabase())
    }

    override fun getChatMessages(): Flow<List<DiaryChatMessage>> = database.getChatMessages().map { chatMessages ->
        chatMessages.map { it.toDiaryChatMessage() }
    }
}
