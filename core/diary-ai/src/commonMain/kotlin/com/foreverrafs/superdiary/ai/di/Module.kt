package com.foreverrafs.superdiary.ai.di

import com.foreverrafs.superdiary.ai.DiaryAiImpl
import com.foreverrafs.superdiary.ai.api.DiaryAI
import com.foreverrafs.superdiary.ai.domain.usecase.GetChatMessagesUseCase
import com.foreverrafs.superdiary.ai.domain.usecase.SaveChatMessageUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val diaryAiModule: Module = module {
    factoryOf(::DiaryAiImpl) { bind<DiaryAI>() }

    factoryOf(::GetChatMessagesUseCase)
    factoryOf(::SaveChatMessageUseCase)
}
