package com.foreverrafs.superdiary.diary.diaryai

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.LoggingConfig
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
        logging = LoggingConfig(logLevel = LogLevel.None),
    )

    private val generateDiaryMessages = mutableSetOf<ChatMessage>()
    private val diaryChatMessages = mutableSetOf<ChatMessage>()

    private val weeklyDiaryGeneratorPrompt = """
        You are Journal AI. I will give you a combined list of entries written over a period of 
        one week and you write a brief, concise and informative 50 word summary for me. The summary
        should be in the first person narrative.
    """.trimIndent()

    private val diaryChatPrompt = """
        You are Journal AI, I will provide you a list of journal entries and their dates and you will 
        respond to follow up questions based on this information. You are not supposed to respond to 
        any questions outside of the scope of the data you have been given under any circumstances.
        Your responses should be very concise and if you don't have the answer to a question, simply let
        the user know that you are only able to assist with information contained in their entries.
        You should also encourage users to write more entries so that you can be more useful to them.
    """.trimIndent()

    override fun generateDiary(
        prompt: String,
        wordCount: Int,
    ): Flow<String> {
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
            model = ModelId("gpt-4-1106-preview"),
            messages = generateDiaryMessages.toList(),
        )

        var assistantMessages: String? = null
        return openAi.chatCompletions(chatCompletionRequest)
            .map {
                it.choices.first().delta.content.orEmpty()
            }.onEach {
                assistantMessages += it
            }.onCompletion { error ->
                if (error == null) {
                    generateDiaryMessages.add(
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
        generateDiaryMessages.add(
            ChatMessage(
                role = ChatRole.System,
                content = weeklyDiaryGeneratorPrompt,
            ),
        )

        // Add the prompt
        generateDiaryMessages.add(
            ChatMessage(
                role = ChatRole.User,
                content = diaries.joinToString { it.entry },
            ),
        )

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId(GPT_MODEL),
            messages = generateDiaryMessages.toList(),
        )

        return openAi.chatCompletion(chatCompletionRequest).choices.first().message.content
            ?: "Error"
    }

    override fun generateWeeklySummaryAsync(diaries: List<Diary>): Flow<String> {
        generateDiaryMessages.clear()

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

        return openAi.chatCompletions(request).mapNotNull {
            it.choices.first().delta.content
        }
    }

    override suspend fun queryDiaries(diaries: List<Diary>, query: String): String {
        // Add the instruction
        diaryChatMessages.add(
            ChatMessage.System(
                content = diaryChatPrompt,
            ),
        )
        // Add the data
        diaryChatMessages.add(
            ChatMessage.User(
                content = diaries.joinToString(),
            ),
        )

        // Add the query
        diaryChatMessages.add(
            ChatMessage.User(
                content = query,
            ),
        )

        val request = ChatCompletionRequest(
            model = ModelId(GPT_MODEL),
            messages = diaryChatMessages.toList(),
        )

        return openAi.chatCompletion(request).choices.first().message.content.orEmpty()
    }

    companion object {
        private const val GPT_MODEL = "gpt-4-1106-preview"
    }
}
