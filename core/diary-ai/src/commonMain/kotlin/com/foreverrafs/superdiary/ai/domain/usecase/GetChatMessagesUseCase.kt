package com.foreverrafs.superdiary.ai.domain.usecase

import com.foreverrafs.superdiary.ai.domain.model.DiaryChatMessage
import com.foreverrafs.superdiary.ai.domain.repository.DiaryAiRepository
import kotlinx.coroutines.flow.Flow

class GetChatMessagesUseCase(
    private val repository: DiaryAiRepository,
) {
    operator fun invoke(): Flow<List<DiaryChatMessage>> =
        repository.getChatMessages()
}
