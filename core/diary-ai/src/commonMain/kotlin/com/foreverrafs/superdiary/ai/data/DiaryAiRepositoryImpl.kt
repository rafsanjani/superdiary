package com.foreverrafs.superdiary.ai.data

import com.foreverrafs.superdiary.ai.api.DiaryAI
import com.foreverrafs.superdiary.ai.domain.model.DiaryChatMessage
import com.foreverrafs.superdiary.ai.domain.model.toDatabase
import com.foreverrafs.superdiary.ai.domain.model.toDiaryChatMessage
import com.foreverrafs.superdiary.ai.domain.repository.DiaryAiRepository
import com.foreverrafs.superdiary.database.Database
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.model.WeeklySummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class DiaryAiRepositoryImpl(
    private val database: Database,
    private val diaryAI: DiaryAI,
) : DiaryAiRepository {
    override fun generateDiary(prompt: String, wordCount: Int): Flow<String> =
        diaryAI.generateDiary(prompt, wordCount)

    override fun generateSummary(
        diaries: List<Diary>,
        onCompletion: (WeeklySummary?) -> Unit,
    ): Flow<String> =
        diaryAI.generateSummary(diaries, onCompletion)

    override suspend fun saveChatMessage(message: DiaryChatMessage) =
        database.saveChatMessage(message.toDatabase())

    override fun getChatMessages(): Flow<List<DiaryChatMessage>> =
        database.getChatMessages()
            .map { chatMessagesDb -> chatMessagesDb.map { it.toDiaryChatMessage() } }

    override suspend fun clearChatMessages() = database.clearChatMessages()
}
