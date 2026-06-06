package com.foreverrafs.superdiary.chat.data

import com.foreverrafs.superdiary.ai.api.DiaryAI
import com.foreverrafs.superdiary.ai.domain.model.DiaryChatMessage
import com.foreverrafs.superdiary.ai.domain.model.toDatabase
import com.foreverrafs.superdiary.ai.domain.model.toDiaryChatMessage
import com.foreverrafs.superdiary.chat.domain.repository.DiaryChatRepository
import com.foreverrafs.superdiary.data.mapper.toDiary
import com.foreverrafs.superdiary.database.Database
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.repository.DataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class DiaryChatRepositoryImpl(
    private val dataSource: DataSource,
    private val diaryAI: DiaryAI,
) : DiaryChatRepository {
    override fun getAllDiaries(): Flow<List<Diary>> = emptyFlow()

    override suspend fun queryDiaries(messages: List<DiaryChatMessage>): String? = try {
        diaryAI.queryDiaries(messages)
    } catch (_: Exception) {
        null
    }

    override suspend fun saveChatMessage(message: DiaryChatMessage) {
    }

    override fun getChatMessages(): Flow<List<DiaryChatMessage>> = emptyFlow()
}
