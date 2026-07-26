package com.foreverrafs.superdiary.ai.di

import ai.koog.http.client.ktor.KtorKoogHttpClient
import ai.koog.prompt.executor.clients.openai.OpenAIClientSettings
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.llms.MultiLLMPromptExecutor
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.llm.DeepSeekLLMProvider
import ai.koog.prompt.llm.LLMProvider
import com.foreverrafs.superdiary.ai.DiaryAiImpl
import com.foreverrafs.superdiary.ai.api.DiaryAI
import com.foreverrafs.superdiary.core.SuperDiarySecret
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

object DeepSeekClient : OpenAILLMClient(
    apiKey = SuperDiarySecret.openAIKey,
    httpClientFactory = KtorKoogHttpClient.Factory(),
    settings = OpenAIClientSettings(
        baseUrl = "https://api.deepseek.com",
    ),
) {
    override fun llmProvider(): LLMProvider = DeepSeekLLMProvider
}

val diaryAiModule: Module = module {
    single<PromptExecutor> {
        MultiLLMPromptExecutor(
            DeepSeekClient,
        )
    }
    factoryOf(::DiaryAiImpl) { bind<DiaryAI>() }
}
