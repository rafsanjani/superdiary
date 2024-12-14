package com.foreverrafs.superdiary.data.diaryai

import com.benasher44.uuid.uuid4
import com.foreverrafs.superdiary.database.model.DiaryChatMessageDb
import com.foreverrafs.superdiary.database.model.DiaryChatRoleDb
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Suppress("FunctionName")
data class DiaryChatMessage(
    val id: String,
    val role: DiaryChatRole,
    val timestamp: Instant,
    val content: String,
) {
    companion object {
        fun User(content: String) = DiaryChatMessage(
            id = uuid4().toString(),
            timestamp = Clock.System.now(),
            role = DiaryChatRole.User,
            content = content,
        )

        fun DiaryAI(content: String) = DiaryChatMessage(
            id = uuid4().toString(),
            timestamp = Clock.System.now(),
            role = DiaryChatRole.DiaryAI,
            content = content,
        )

        fun System(content: String) = DiaryChatMessage(
            id = uuid4().toString(),
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

fun DiaryChatRole.toDatabase() = DiaryChatRoleDb.valueOf(name)
