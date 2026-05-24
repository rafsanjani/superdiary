package com.foreverrafs.superdiary.ai

import ai.koog.agents.core.tools.ToolDescriptor
import ai.koog.prompt.Prompt
import ai.koog.prompt.dsl.ModerationResult
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.llm.LLModel
import ai.koog.prompt.message.Message
import ai.koog.prompt.message.ResponseMetaInfo
import ai.koog.prompt.streaming.StreamFrame
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakePromptExecutor : PromptExecutor() {
    var executeCalls = 0
        private set
    var executeStreamingCalls = 0
        private set

    fun reset() {
        executeCalls = 0
        executeStreamingCalls = 0
    }

    override suspend fun execute(
        prompt: Prompt,
        model: LLModel,
        tools: List<ToolDescriptor>,
    ): Message.Assistant {
        executeCalls++
        return Message.Assistant(
            content = "",
            metaInfo = ResponseMetaInfo.Empty,
        )
    }

    override fun executeStreaming(
        prompt: Prompt,
        model: LLModel,
        tools: List<ToolDescriptor>,
    ): Flow<StreamFrame> {
        executeStreamingCalls++
        return flowOf()
    }

    override suspend fun moderate(prompt: Prompt, model: LLModel): ModerationResult {
        error("Moderation is not used by these tests")
    }

    override suspend fun models(): List<LLModel> = listOf(OpenAIModels.Chat.GPT5_1)

    override fun close() = Unit
}
