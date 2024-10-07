package com.foreverrafs.superdiary.data.usecase

import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.diaryai.DiaryChatMessage

class SaveChatMessageUseCase(
    private val dataSource: DataSource,
) {
    suspend operator fun invoke(message: DiaryChatMessage): Unit =
        dataSource.saveChatMessage(message)
}
