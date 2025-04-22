package com.foreverrafs.superdiary.ai.domain.model

import com.aallam.openai.api.chat.ChatMessage
import com.foreverrafs.superdiary.database.model.DiaryChatMessageDb
import com.foreverrafs.superdiary.database.model.DiaryChatRoleDb
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Suppress("FunctionName")
data class DiaryChatMessage(
    val id: String,
    val role: DiaryChatRole,
    val timestamp: Instant,
    val content: String,
) {
    @OptIn(ExperimentalUuidApi::class)
    companion object {
        fun User(content: String) = DiaryChatMessage(
            id = Uuid.random().toString(),
            timestamp = Clock.System.now(),
            role = DiaryChatRole.User,
            content = content,
        )

        fun DiaryAI(content: String) = DiaryChatMessage(
            id = Uuid.random().toString(),
            timestamp = Clock.System.now(),
            role = DiaryChatRole.DiaryAI,
            content = content,
        )

        fun System(content: String) = DiaryChatMessage(
            id = Uuid.random().toString(),
            timestamp = Clock.System.now(),
            role = DiaryChatRole.System,
            content = content,
        )
    }
}

fun DiaryChatMessage.toDatabase() = DiaryChatMessageDb(
    id = id,
    role = role.toDatabase(),
    timestamp = timestamp,
    content = content,
)

fun DiaryChatMessage.toNetworkChatMessage() = when (role) {
    DiaryChatRole.User -> ChatMessage.User(content)
    DiaryChatRole.DiaryAI -> ChatMessage.Assistant(content)
    DiaryChatRole.System -> ChatMessage.System(content)
}

fun DiaryChatMessageDb.toDiaryChatMessage(): DiaryChatMessage = when (role) {
    DiaryChatRoleDb.User -> DiaryChatMessage.User(content)
    DiaryChatRoleDb.DiaryAI -> DiaryChatMessage.DiaryAI(content)
    DiaryChatRoleDb.System -> DiaryChatMessage.System(content)
}
