package com.foreverrafs.superdiary.ai.domain.usecase

import com.foreverrafs.superdiary.ai.domain.model.DiaryChatMessage
import com.foreverrafs.superdiary.ai.domain.repository.DiaryAiRepository

class SaveChatMessageUseCase(
    private val repository: DiaryAiRepository,
) {
    suspend operator fun invoke(message: DiaryChatMessage): Unit =
        repository.saveChatMessage(message)
}
