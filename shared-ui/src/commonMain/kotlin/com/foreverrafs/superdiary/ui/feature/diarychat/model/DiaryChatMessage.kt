package com.foreverrafs.superdiary.ui.feature.diarychat.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.random.Random

@Suppress("FunctionName")
data class DiaryChatMessage(
    val id: Long,
    val chatRole: ChatRole,
    val timestamp: Instant,
    val content: String,
) {
    companion object {
        fun User(content: String) = DiaryChatMessage(
            id = Random.nextLong(),
            timestamp = Clock.System.now(),
            chatRole = ChatRole.User,
            content = content,
        )

        fun DiaryAI(content: String) = DiaryChatMessage(
            id = Random.nextLong(),
            timestamp = Clock.System.now(),
            chatRole = ChatRole.DiaryAI,
            content = content,
        )
    }
}
