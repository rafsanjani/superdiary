package com.foreverrafs.superdiary.diary.generator

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Duration.Companion.seconds

class SuperDiaryAI : DiaryAI {
    private val openAi = OpenAI(
        token = "my-token",
        timeout = Timeout(socket = 60.seconds),
    )

    override fun generateDiary(prompt: String, wordCount: Int): Flow<String> {
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.System,
                    content = "You are Journal AI, you can rewrite every statement I give you into a $wordCount word journal. You are not supposed to write anything yet. ",
                ),
                ChatMessage(
                    role = ChatRole.System,
                    content = prompt,
                ),
            ),
        )

        return openAi.chatCompletions(chatCompletionRequest)
            .map { it.choices.first().delta.content.orEmpty() }
    }
}
