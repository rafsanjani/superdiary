package com.foreverrafs.superdiary.ai

import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.streaming.StreamFrame
import com.foreverrafs.superdiary.ai.api.DiaryAI
import com.foreverrafs.superdiary.ai.domain.model.DiaryChatMessage
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.model.WeeklySummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** A diary AI implementation using Open AI */
class DiaryAiImpl(
    private val logger: AggregateLogger,
    private val promptExecutor: PromptExecutor,
) : DiaryAI {

    override fun generateDiary(
        prompt: String,
        wordCount: Int,
    ): Flow<String> {
        // Add the instruction
        val systemMessage = """
                    You are Journal AI, you can rewrite every statement I give you into a $wordCount word informal journal.
                    You are not supposed to write anything yet and you do not respond to questions. You are very empathic and
                    should avoid the use of any foul or deeply strong language. You can be witty at times and carry a bit of humor.
                    You will never make a reference to the fact that you are an AI no matter what.
        """.trimIndent()

        return promptExecutor.executeStreaming(
            prompt = prompt("generate-diary") {
                system {
                    text(systemMessage)
                }

                user {
                    text(prompt)
                }
            },
            model = GPT_MODEL,
        ).map { response ->
            when (response) {
                is StreamFrame.Append -> response.text

                is StreamFrame.End -> {
                    logger.i(TAG) {
                        "Diary generation completed: ${response.finishReason}"
                    }
                    ""
                }

                is StreamFrame.ToolCall -> {
                    logger.i(TAG) {
                        "Diary generation completed: ${response.content}"
                    }
                    ""
                }
            }
        }
    }

    override fun generateSummary(
        diaries: List<Diary>,
        onCompletion: suspend (WeeklySummary?) -> Unit,
    ): Flow<String> {
        val weeklySummaryGeneratorPrompt = """
            You are Journal AI. I will give you a combined list of entries written over a period of
            one week and you write a brief, concise and informative summary for me. It should be at
            least 50 words and at most 100. The grammar should be spot on without any mistakes or errors.
            Make sure you punctuate it properly as well. This should be in the first person narrative.
        """.trimIndent()

        var totalSummary = ""
        return promptExecutor.executeStreaming(
            prompt = prompt("generate-diary") {
                // Add the instruction
                system {
                    text(weeklySummaryGeneratorPrompt)
                }

                // Add the prompt
                user {
                    text(diaries.joinToString { it.entry })
                }
            },
            model = GPT_MODEL,
        ).map { response ->
            when (response) {
                is StreamFrame.Append -> {
                    totalSummary += response.text
                    totalSummary
                }

                is StreamFrame.End -> {
                    logger.i(TAG) {
                        "Diary generation completed: ${response.finishReason}"
                    }
                    onCompletion(
                        WeeklySummary(
                            summary = totalSummary,
                        ),
                    )
                    totalSummary
                }

                is StreamFrame.ToolCall -> {
                    logger.i(TAG) {
                        "Diary generation completed: ${response.content}"
                    }
                    ""
                }
            }
        }
    }

    override suspend fun queryDiaries(
        messages: List<DiaryChatMessage>,
    ): String = try {
        promptExecutor.execute(
            prompt = prompt("generate-diary") {
                // Add the instruction
                system {
                    text(
                        text = messages.take(
                            (messages.size - 1).coerceAtLeast(0),
                        ).joinToString(separator = "\n"),
                    )
                }

                // Add the prompt
                user {
                    text(messages.lastOrNull()?.content.orEmpty())
                }
            },
            model = GPT_MODEL,
        ).firstOrNull()?.content.orEmpty()
    } catch (e: Exception) {
        logger.e(TAG, e) { "Error querying diaries" }
        ""
    }

    companion object {
        private val GPT_MODEL = OpenAIModels.Chat.GPT5_1
        private const val TAG = "OpenDiaryAI"
    }
}
