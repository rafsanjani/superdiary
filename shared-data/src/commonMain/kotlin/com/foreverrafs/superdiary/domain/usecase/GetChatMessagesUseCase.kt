package com.foreverrafs.superdiary.domain.usecase

import com.foreverrafs.superdiary.data.diaryai.DiaryChatMessage
import com.foreverrafs.superdiary.domain.repository.DataSource
import kotlinx.coroutines.flow.Flow

class GetChatMessagesUseCase(
    private val dataSource: DataSource,
) {
    operator fun invoke(): Flow<List<DiaryChatMessage>> =
        dataSource.getChatMessages()
}
