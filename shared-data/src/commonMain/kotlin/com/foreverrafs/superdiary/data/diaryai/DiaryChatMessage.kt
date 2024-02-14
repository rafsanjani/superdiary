package com.foreverrafs.superdiary.data.diaryai

import kotlin.random.Random
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Suppress("FunctionName")
data class DiaryChatMessage(
    val id: Long,
    val role: DiaryChatRole,
    val timestamp: Instant,
    val content: String,
) {
    companion object {
        fun User(content: String) = DiaryChatMessage(
            id = Random.nextLong(),
            timestamp = Clock.System.now(),
            role = DiaryChatRole.User,
            content = content,
        )

        fun DiaryAI(content: String) = DiaryChatMessage(
            id = Random.nextLong(),
            timestamp = Clock.System.now(),
            role = DiaryChatRole.DiaryAI,
            content = content,
        )

        fun System(content: String) = DiaryChatMessage(
            id = Random.nextLong(),
            timestamp = Clock.System.now(),
            role = DiaryChatRole.System,
            content = content,
        )
    }
}
