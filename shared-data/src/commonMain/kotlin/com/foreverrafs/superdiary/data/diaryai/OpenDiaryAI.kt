package com.foreverrafs.superdiary.data.diaryai

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.model.Diary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach

/** A diary AI implementation using Open AI */
class OpenDiaryAI(
    private val openAI: OpenAI,
    private val logger: AggregateLogger,
) : DiaryAI {
    override fun generateDiary(
        prompt: String,
        wordCount: Int,
    ): Flow<String> {
        val generateDiaryMessages = mutableListOf<ChatMessage>()
        // Add the instruction
        generateDiaryMessages.add(
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
        generateDiaryMessages.add(
            ChatMessage(
                role = ChatRole.User,
                content = prompt,
            ),
        )

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId(GPT_MODEL),
            messages = generateDiaryMessages.toList(),
        )

        var assistantMessages: String? = null
        return openAI.chatCompletions(chatCompletionRequest).map {
            it.choices.first().delta?.content.orEmpty()
        }.onEach {
            assistantMessages += it
        }.onCompletion { error ->
            if (error != null) {
                logger.e(tag = TAG, throwable = error)
                return@onCompletion
            }
            generateDiaryMessages.add(
                ChatMessage(
                    role = ChatRole.Assistant,
                    content = assistantMessages,
                ),
            )
        }
    }

    override fun getWeeklySummary(diaries: List<Diary>): Flow<String> {
        val weeklyDiaryGeneratorPrompt = """
            You are Journal AI. I will give you a combined list of entries written over a period of
            one week and you write a brief, concise and informative summary for me. It should be at
            least 50 words and at most 100. The grammar should be spot on without any mistakes or errors.
            Make sure you punctuate it properly as well. This should be in the first person narrative.
        """.trimIndent()

        val generateDiaryMessages = mutableListOf<ChatMessage>()
        // Add the instruction
        generateDiaryMessages.add(
            ChatMessage.System(
                content = weeklyDiaryGeneratorPrompt,
            ),
        )

        // Add the prompt
        generateDiaryMessages.add(
            ChatMessage.User(
                content = diaries.joinToString { it.entry },
            ),
        )

        val request = ChatCompletionRequest(
            model = ModelId(GPT_MODEL),
            messages = generateDiaryMessages.toList(),
        )

        return openAI.chatCompletions(request).mapNotNull {
            it.choices.first().delta?.content
        }
    }

    override suspend fun queryDiaries(
        messages: List<DiaryChatMessage>,
    ): String {
        val request = ChatCompletionRequest(
            model = ModelId(GPT_MODEL),
            messages = messages.map { it.toOpenAIChatMessage() },
        )

        return openAI.chatCompletion(request).choices.first().message.content.orEmpty()
    }

    companion object {
        private const val GPT_MODEL = "chatgpt-4o-latest"
        private const val TAG = "OpenDiaryAI"
    }
}
