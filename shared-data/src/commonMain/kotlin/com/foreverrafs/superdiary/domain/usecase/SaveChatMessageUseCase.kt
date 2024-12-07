package com.foreverrafs.superdiary.domain.usecase

import com.foreverrafs.superdiary.data.diaryai.DiaryChatMessage
import com.foreverrafs.superdiary.domain.repository.DataSource

class SaveChatMessageUseCase(
    private val dataSource: DataSource,
) {
    suspend operator fun invoke(message: DiaryChatMessage): Unit =
        dataSource.saveChatMessage(message)
}
