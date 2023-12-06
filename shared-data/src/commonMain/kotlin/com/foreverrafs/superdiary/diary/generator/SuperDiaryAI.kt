package com.foreverrafs.superdiary.diary.generator

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.foreverrafs.superdiary.buildKonfig.BuildKonfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlin.time.Duration.Companion.seconds

class SuperDiaryAI : DiaryAI {
    private val openAi = OpenAI(
        token = BuildKonfig.openAIKey,
        timeout = Timeout(socket = 15.seconds),
    )

    private var messages = mutableSetOf<ChatMessage>()

    override fun generateDiary(
        prompt: String,
        wordCount: Int,
    ): Flow<String> {
        // Add the instruction
        messages.add(
            ChatMessage(
                role = ChatRole.System,
                content = """
                    You are Journal AI, you can rewrite every statement I give you into a $wordCount word informal journal.
                    You are not supposed to write anything yet and you do not respond to questions. You are very empathic and 
                    should avoid the use of any foul or deeply strong language. You can be witty at times and carry a bit of humor.
                    You will never make a reference to the fact that you are an AI no matter what. 
                """.trimIndent(),
            ),
        )

        // Add the prompt
        messages.add(
            ChatMessage(
                role = ChatRole.User,
                content = prompt,
            ),
        )

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-4-1106-preview"),
            messages = messages.toList(),
        )

        var assistantMessages: String? = null
        return openAi.chatCompletions(chatCompletionRequest)
            .map {
                it.choices.first().delta.content.orEmpty()
            }.onEach {
                assistantMessages += it
            }.onCompletion { error ->
                if (error == null) {
                    messages.add(
                        ChatMessage(
                            role = ChatRole.Assistant,
                            content = assistantMessages,
                        ),
                    )
                }
            }
    }
}
