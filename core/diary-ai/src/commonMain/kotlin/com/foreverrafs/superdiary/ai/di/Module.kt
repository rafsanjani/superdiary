package com.foreverrafs.superdiary.ai.di

import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import ai.koog.prompt.executor.model.PromptExecutor
import com.foreverrafs.superdiary.ai.DiaryAiImpl
import com.foreverrafs.superdiary.ai.api.DiaryAI
import com.foreverrafs.superdiary.core.SuperDiarySecret
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val diaryAiModule: Module = module {
    single<PromptExecutor> {
        simpleGoogleAIExecutor(SuperDiarySecret.openAIKey)
    }
    factoryOf(::DiaryAiImpl) { bind<DiaryAI>() }
}
