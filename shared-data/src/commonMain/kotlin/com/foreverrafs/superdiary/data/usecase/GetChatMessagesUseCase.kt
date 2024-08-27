package com.foreverrafs.superdiary.data.usecase

import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.diaryai.DiaryChatMessage
import kotlinx.coroutines.flow.Flow

class GetChatMessagesUseCase(
    private val dataSource: DataSource,
) {
    operator fun invoke(): Flow<List<DiaryChatMessage>> =
        dataSource.getChatMessages()
}
