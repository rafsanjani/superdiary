package com.foreverrafs.superdiary.data.di

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.foreverrafs.superdiary.buildKonfig.BuildKonfig
import com.foreverrafs.superdiary.data.Database
import com.foreverrafs.superdiary.data.analytics.Analytics
import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.datasource.LocalDataSource
import com.foreverrafs.superdiary.data.diaryai.DiaryAI
import com.foreverrafs.superdiary.data.diaryai.OpenDiaryAI
import com.foreverrafs.superdiary.data.usecase.AddDiaryUseCase
import com.foreverrafs.superdiary.data.usecase.AddWeeklySummaryUseCase
import com.foreverrafs.superdiary.data.usecase.CalculateBestStreakUseCase
import com.foreverrafs.superdiary.data.usecase.CalculateStreakUseCase
import com.foreverrafs.superdiary.data.usecase.CountDiariesUseCase
import com.foreverrafs.superdiary.data.usecase.DeleteDiaryUseCase
import com.foreverrafs.superdiary.data.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.data.usecase.GetFavoriteDiariesUseCase
import com.foreverrafs.superdiary.data.usecase.GetLatestEntriesUseCase
import com.foreverrafs.superdiary.data.usecase.GetWeeklySummaryUseCase
import com.foreverrafs.superdiary.data.usecase.SearchDiaryBetweenDatesUseCase
import com.foreverrafs.superdiary.data.usecase.SearchDiaryByDateUseCase
import com.foreverrafs.superdiary.data.usecase.SearchDiaryByEntryUseCase
import com.foreverrafs.superdiary.data.usecase.UpdateDiaryUseCase
import com.foreverrafs.superdiary.data.validator.DiaryValidator
import com.foreverrafs.superdiary.data.validator.DiaryValidatorImpl
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
