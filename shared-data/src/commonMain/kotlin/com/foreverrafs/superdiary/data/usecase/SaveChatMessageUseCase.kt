package com.foreverrafs.superdiary.data.usecase

import com.foreverrafs.superdiary.core.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.diaryai.DiaryChatMessage
import kotlinx.coroutines.withContext

class SaveChatMessageUseCase(
    private val dataSource: DataSource,
    private val dispatchers: AppCoroutineDispatchers,
) {
    suspend operator fun invoke(message: DiaryChatMessage) = withContext(dispatchers.io) {
        dataSource.saveChatMessage(message)
    }
}
