package com.foreverrafs.superdiary.ai.di

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.foreverrafs.superdiary.ai.DiaryAiImpl
import com.foreverrafs.superdiary.ai.api.DiaryAI
import com.foreverrafs.superdiary.ai.data.DiaryAiRepositoryImpl
import com.foreverrafs.superdiary.ai.domain.repository.DiaryAiRepository
import com.foreverrafs.superdiary.ai.domain.usecase.GetChatMessagesUseCase
import com.foreverrafs.superdiary.ai.domain.usecase.SaveChatMessageUseCase
import com.foreverrafs.superdiary.core.SuperDiarySecret
import kotlin.time.Duration.Companion.seconds
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val diaryAiModule: Module = module {
    single<OpenAI> {
        OpenAI(
            token = SuperDiarySecret.openAIKey,
            timeout = Timeout(socket = 15.seconds),
            logging = LoggingConfig(logLevel = LogLevel.None),
        )
    }

    factoryOf(::DiaryAiRepositoryImpl) { bind<DiaryAiRepository>() }

    factoryOf(::DiaryAiImpl) { bind<DiaryAI>() }

    factoryOf(::GetChatMessagesUseCase)
    factoryOf(::SaveChatMessageUseCase)
}
