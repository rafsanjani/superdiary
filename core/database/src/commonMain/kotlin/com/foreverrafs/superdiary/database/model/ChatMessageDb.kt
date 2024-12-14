@file:OptIn(ExperimentalUuidApi::class)

package com.foreverrafs.superdiary.database.model

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Suppress("FunctionName")
data class DiaryChatMessageDb(
    val id: String,
    val role: DiaryChatRoleDb,
    val timestamp: Instant,
    val content: String,
) {
    companion object {
        fun User(content: String) = DiaryChatMessageDb(
            id = Uuid.random().toString(),
            timestamp = Clock.System.now(),
            role = DiaryChatRoleDb.User,
            content = content,
        )

        fun DiaryAI(content: String) = DiaryChatMessageDb(
            id = Uuid.random().toString(),
            timestamp = Clock.System.now(),
            role = DiaryChatRoleDb.DiaryAI,
            content = content,
        )

        fun System(content: String) = DiaryChatMessageDb(
            id = Uuid.random().toString(),
            timestamp = Clock.System.now(),
            role = DiaryChatRoleDb.System,
            content = content,
        )
    }
}

enum class DiaryChatRoleDb {
    // User queries
    User,

    // AI Responses
    DiaryAI,

    // System instructions
    System,
}
