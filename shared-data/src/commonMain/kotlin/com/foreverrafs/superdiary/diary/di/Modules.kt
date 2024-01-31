package com.foreverrafs.superdiary.diary.di

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.foreverrafs.superdiary.buildKonfig.BuildKonfig
import com.foreverrafs.superdiary.diary.Database
import com.foreverrafs.superdiary.diary.analytics.Analytics
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.datasource.LocalDataSource
import com.foreverrafs.superdiary.diary.diaryai.DiaryAI
import com.foreverrafs.superdiary.diary.diaryai.OpenDiaryAI
import com.foreverrafs.superdiary.diary.usecase.AddDiaryUseCase
import com.foreverrafs.superdiary.diary.usecase.AddWeeklySummaryUseCase
import com.foreverrafs.superdiary.diary.usecase.CalculateBestStreakUseCase
import com.foreverrafs.superdiary.diary.usecase.CalculateStreakUseCase
import com.foreverrafs.superdiary.diary.usecase.CountDiariesUseCase
import com.foreverrafs.superdiary.diary.usecase.DeleteDiaryUseCase
import com.foreverrafs.superdiary.diary.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.diary.usecase.GetFavoriteDiariesUseCase
import com.foreverrafs.superdiary.diary.usecase.GetLatestEntriesUseCase
import com.foreverrafs.superdiary.diary.usecase.GetWeeklySummaryUseCase
import com.foreverrafs.superdiary.diary.usecase.SearchDiaryBetweenDatesUseCase
import com.foreverrafs.superdiary.diary.usecase.SearchDiaryByDateUseCase
import com.foreverrafs.superdiary.diary.usecase.SearchDiaryByEntryUseCase
import com.foreverrafs.superdiary.diary.usecase.UpdateDiaryUseCase
import com.foreverrafs.superdiary.diary.validator.DiaryValidator
import com.foreverrafs.superdiary.diary.validator.DiaryValidatorImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import kotlin.time.Duration.Companion.seconds

fun useCaseModule() = module {
    single<DataSource> { LocalDataSource(get()) }
    factory<Clock> { Clock.System }
    factory<CoroutineDispatcher> { Dispatchers.Default }

    single<OpenAI> {
        OpenAI(
            token = BuildKonfig.openAIKey,
            timeout = Timeout(socket = 15.seconds),
            logging = LoggingConfig(logLevel = LogLevel.None),
        )
    }
    factory<DiaryAI> { OpenDiaryAI(openAI = get()) }
    factoryOf(::AddDiaryUseCase)
    factoryOf(::GetAllDiariesUseCase)
    factoryOf(::GetFavoriteDiariesUseCase)
    factoryOf(::SearchDiaryBetweenDatesUseCase)
    factoryOf(::SearchDiaryByEntryUseCase)
    factoryOf(::SearchDiaryByEntryUseCase)
    factoryOf(::SearchDiaryByDateUseCase)
    factoryOf(::DeleteDiaryUseCase)
    singleOf(::UpdateDiaryUseCase)
    singleOf(::Database)
    factory<DiaryValidator> { DiaryValidatorImpl(get()) }
    factoryOf(::GetLatestEntriesUseCase)
    factoryOf(::CountDiariesUseCase)
    factoryOf(::CalculateStreakUseCase)
    factoryOf(::AddWeeklySummaryUseCase)
    factoryOf(::GetWeeklySummaryUseCase)
    factoryOf(::CalculateBestStreakUseCase)
}

expect fun platformModule(analytics: Analytics): Module
