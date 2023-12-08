package com.foreverrafs.superdiary.diary.generator

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.foreverrafs.superdiary.buildKonfig.BuildKonfig
import com.foreverrafs.superdiary.diary.model.Diary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlin.time.Duration.Companion.seconds

class SuperDiaryAI : DiaryAI {
    private val openAi = OpenAI(
        token = BuildKonfig.openAIKey,
        timeout = Timeout(socket = 15.seconds),
    )

    private var messages = mutableSetOf<ChatMessage>()

    private val weeklyDiaryGeneratorPrompt = """
        You are Journal AI. I will give you a combined list of entries written over a period of 
        one week and you write a brief, concise and informative 50 word summary for me. The summary
        should be in the first person narrative.
    """.trimIndent()

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

    override suspend fun generateWeeklySummary(diaries: List<Diary>): String {
        // Add the instruction
        messages.add(
            ChatMessage(
                role = ChatRole.System,
                content = weeklyDiaryGeneratorPrompt,
            ),
        )

        // Add the prompt
        messages.add(
            ChatMessage(
                role = ChatRole.User,
                content = diaries.joinToString { it.entry },
            ),
        )

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId(GPT_MODEL),
            messages = messages.toList(),
        )

        return openAi.chatCompletion(chatCompletionRequest).choices.first().message.content
            ?: "Error"
    }

    override fun generateWeeklySummaryAsync(diaries: List<Diary>): Flow<String> {
        // Add the instruction
        messages.add(
            ChatMessage(
                role = ChatRole.System,
                content = weeklyDiaryGeneratorPrompt,
            ),
        )

        // Add the prompt
        messages.add(
            ChatMessage(
                role = ChatRole.User,
                content = diaries.joinToString { it.entry },
            ),
        )

        val request = ChatCompletionRequest(
            model = ModelId(GPT_MODEL),
            messages = messages.toList(),
        )

        return openAi.chatCompletions(request).mapNotNull {
            it.choices.first().delta.content
        }
    }

    companion object {
        private const val GPT_MODEL = "gpt-4-1106-preview"
    }
}
