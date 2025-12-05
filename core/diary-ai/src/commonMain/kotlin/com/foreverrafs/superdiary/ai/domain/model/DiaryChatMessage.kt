package com.foreverrafs.superdiary.ai.domain.model

import com.foreverrafs.superdiary.database.model.DiaryChatMessageDb
import com.foreverrafs.superdiary.database.model.DiaryChatRoleDb
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Suppress("FunctionName")
data class DiaryChatMessage(
    val id: String,
    val role: DiaryChatRole,
    val timestamp: kotlin.time.Instant,
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

fun DiaryChatMessageDb.toDiaryChatMessage(): DiaryChatMessage = when (role) {
    DiaryChatRoleDb.User -> DiaryChatMessage.User(content)
    DiaryChatRoleDb.DiaryAI -> DiaryChatMessage.DiaryAI(content)
    DiaryChatRoleDb.System -> DiaryChatMessage.System(content)
}
