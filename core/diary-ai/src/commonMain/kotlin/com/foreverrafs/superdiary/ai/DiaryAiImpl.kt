package com.foreverrafs.superdiary.ai

import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.llm.DeepSeekLLMProvider
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLModel
import ai.koog.prompt.message.MessagePart
import ai.koog.prompt.streaming.StreamFrame
import com.foreverrafs.superdiary.ai.api.DiaryAI
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.model.WeeklySummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** A diary AI implementation using OpenAI */
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
            model = CHAT_MODEL,
        ).map { response ->
            when (response) {
                is StreamFrame.TextDelta -> response.text

                is StreamFrame.TextComplete -> response.text

                is StreamFrame.ReasoningDelta,
                is StreamFrame.ReasoningComplete,
                -> ""

                is StreamFrame.End -> {
                    logger.i(TAG) {
                        "Diary generation completed: ${response.finishReason}"
                    }
                    ""
                }

                is StreamFrame.ToolCallDelta -> {
                    logger.i(TAG) {
                        "Diary generation completed: ${response.content}"
                    }
                    ""
                }

                is StreamFrame.ToolCallComplete -> {
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
            model = CHAT_MODEL,
        ).map { response ->
            when (response) {
                is StreamFrame.TextDelta -> {
                    totalSummary += response.text
                    totalSummary
                }

                is StreamFrame.TextComplete -> {
                    totalSummary = response.text
                    totalSummary
                }

                is StreamFrame.ReasoningDelta,
                is StreamFrame.ReasoningComplete,
                -> totalSummary

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

                is StreamFrame.ToolCallDelta -> {
                    logger.i(TAG) {
                        "Diary generation completed: ${response.content}"
                    }
                    ""
                }

                is StreamFrame.ToolCallComplete -> {
                    logger.i(TAG) {
                        "Diary generation completed: ${response.content}"
                    }
                    ""
                }
            }
        }
    }

    override suspend fun generateWritingInsights(diaries: List<Diary>): String = try {
        if (diaries.isEmpty()) return ""

        val batches = diaries.chunked(MAX_ENTRIES_PER_BATCH)
        if (batches.size == 1) {
            executeWritingAnalysis(
                promptName = "generate-writing-insights",
                systemMessage = FINAL_INSIGHTS_PROMPT,
                content = batches.single().toPromptContent(),
            )
        } else {
            val batchObservations = batches.mapIndexed { index, batch ->
                executeWritingAnalysis(
                    promptName = "analyse-writing-batch-${index + 1}",
                    systemMessage = BATCH_ANALYSIS_PROMPT,
                    content = batch.toPromptContent(),
                )
            }

            executeWritingAnalysis(
                promptName = "synthesise-writing-insights",
                systemMessage = FINAL_INSIGHTS_PROMPT,
                content = batchObservations.joinToString(separator = "\n\n") { observation ->
                    observation.ifBlank { "No reliable pattern found in this batch." }
                },
            )
        }
    } catch (e: Exception) {
        logger.e(TAG, e) { "Error generating writing insights" }
        ""
    }

    private suspend fun executeWritingAnalysis(
        promptName: String,
        systemMessage: String,
        content: String,
    ): String = promptExecutor.execute(
        prompt = prompt(promptName) {
            system {
                text(systemMessage)
            }
            user {
                text(content)
            }
        },
        model = CHAT_MODEL,
    ).parts.filterIsInstance<MessagePart.Text>().joinToString(separator = "") { it.text }

    private fun List<Diary>.toPromptContent(): String =
        joinToString(separator = "\n\n") { diary ->
            """
            Date: ${diary.date}
            Entry: ${diary.entry.take(MAX_ENTRY_CHARACTERS)}
            """.trimIndent()
        }

    companion object {
        private val CHAT_MODEL = LLModel(
            provider = DeepSeekLLMProvider,
            id = "deepseek-chat",
            capabilities = listOf(
                LLMCapability.Completion,
                LLMCapability.OpenAIEndpoint.Completions,
            ),
            contextLength = 65536,
            maxOutputTokens = 8192,
        )
        private const val FINAL_INSIGHTS_PROMPT = """
            You are a thoughtful journal writing coach. Analyse all supplied evidence for writing
            habits only: consistency, entry length, recurring writing styles, level of detail, and
            useful opportunities to build a sustainable writing practice.

            Return exactly these three sections in plain text:
            PATTERNS: A concise observation about what stands out in the writing habits.
            CONSISTENCY: A concise observation about how the writing rhythm or depth changes over time.
            TRY NEXT: One specific and encouraging writing suggestion.

            Stay grounded in the supplied evidence. Do not diagnose mental health, infer sensitive
            personal traits, or present guesses as facts. Do not mention prompts, batches, JSON, or AI.
            Keep the whole response below 180 words.
        """
        private const val BATCH_ANALYSIS_PROMPT = """
            Analyse every supplied journal entry for writing habits only. Capture concise evidence
            about consistency, entry length, writing style, level of detail, and changes over time.
            Do not diagnose mental health or infer sensitive traits. These observations will be
            combined with observations from other entries, so stay factual and below 120 words.
        """
        private const val MAX_ENTRIES_PER_BATCH = 40
        private const val MAX_ENTRY_CHARACTERS = 1_200
        private const val TAG = "OpenDiaryAI"
    }
}
