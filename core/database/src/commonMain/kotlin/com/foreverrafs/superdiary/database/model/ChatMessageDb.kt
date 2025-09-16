@file:OptIn(ExperimentalUuidApi::class)

package com.foreverrafs.superdiary.database.model

import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Suppress("FunctionName")
data class DiaryChatMessageDb(
    val id: String,
    val role: DiaryChatRoleDb,
    val timestamp: kotlin.time.Instant,
    val content: String,
) {
    companion object {
        fun User(content: String) = DiaryChatMessageDb(
            id = Uuid.random().toString(),
            timestamp = kotlin.time.Clock.System.now(),
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
