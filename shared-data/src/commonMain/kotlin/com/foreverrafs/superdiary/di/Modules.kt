package com.foreverrafs.superdiary.di

import com.foreverrafs.superdiary.core.analytics.AnalyticsTracker
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.datasource.LocalDataSource
import com.foreverrafs.superdiary.data.datasource.RemoteDataSource
import com.foreverrafs.superdiary.database.di.databaseModule
import com.foreverrafs.superdiary.domain.repository.DataSource
import com.foreverrafs.superdiary.domain.usecase.AddDiaryUseCase
import com.foreverrafs.superdiary.domain.usecase.AddWeeklySummaryUseCase
import com.foreverrafs.superdiary.domain.usecase.CalculateBestStreakUseCase
import com.foreverrafs.superdiary.domain.usecase.CalculateStreakUseCase
import com.foreverrafs.superdiary.domain.usecase.CountDiariesUseCase
import com.foreverrafs.superdiary.domain.usecase.DeleteDiaryUseCase
import com.foreverrafs.superdiary.domain.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.domain.usecase.GetDiaryByIdUseCase
import com.foreverrafs.superdiary.domain.usecase.GetFavoriteDiariesUseCase
import com.foreverrafs.superdiary.domain.usecase.GetLatestEntriesUseCase
import com.foreverrafs.superdiary.domain.usecase.GetWeeklySummaryUseCase
import com.foreverrafs.superdiary.domain.usecase.SearchDiaryBetweenDatesUseCase
import com.foreverrafs.superdiary.domain.usecase.SearchDiaryByDateUseCase
import com.foreverrafs.superdiary.domain.usecase.SearchDiaryByEntryUseCase
import com.foreverrafs.superdiary.domain.usecase.UpdateDiaryUseCase
import com.foreverrafs.superdiary.domain.validator.DiaryValidator
import com.foreverrafs.superdiary.domain.validator.DiaryValidatorImpl
import com.foreverrafs.superdiary.utils.DiaryPreference
import com.foreverrafs.superdiary.utils.DiaryPreferenceImpl
import kotlinx.datetime.Clock
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val useCaseModule = module {
    includes(databaseModule())

    singleOf(::RemoteDataSource) { bind<DataSource>() }

    // The local datasource will get injected by default
    singleOf(::LocalDataSource) { bind<DataSource>() }

    factory<Clock> { Clock.System }

    factory<DiaryPreference> {
        DiaryPreferenceImpl.getInstance()
    }
    factoryOf(::DiaryValidatorImpl) { bind<DiaryValidator>() }

    factory {
        AddDiaryUseCase(
            dataSource = get<RemoteDataSource>(),
            dispatchers = get(),
            validator = get(),
        )
    }

    factory {
        GetAllDiariesUseCase(
            dataSource = get<RemoteDataSource>(),
            dispatchers = get(),
        )
    }

    factoryOf(::GetFavoriteDiariesUseCase)
    factoryOf(::SearchDiaryBetweenDatesUseCase)
    factoryOf(::SearchDiaryByEntryUseCase)
    factoryOf(::SearchDiaryByDateUseCase)
    factoryOf(::DeleteDiaryUseCase)
    factoryOf(::UpdateDiaryUseCase)
    factoryOf(::GetLatestEntriesUseCase)
    factoryOf(::CountDiariesUseCase)
    factoryOf(::CalculateStreakUseCase)
    factoryOf(::AddWeeklySummaryUseCase)
    factoryOf(::GetWeeklySummaryUseCase)
    factoryOf(::CalculateBestStreakUseCase)
    factory {
        GetDiaryByIdUseCase(
            dispatchers = get(),
            dataSource = get<RemoteDataSource>(),
        )
    }
}

expect fun platformModule(
    analyticsTracker: AnalyticsTracker,
    aggregateLogger: AggregateLogger,
): Module
