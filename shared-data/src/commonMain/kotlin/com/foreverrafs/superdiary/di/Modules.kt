package com.foreverrafs.superdiary.di

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.foreverrafs.preferences.DiaryPreference
import com.foreverrafs.preferences.DiaryPreferenceImpl
import com.foreverrafs.superdiary.core.analytics.AnalyticsTracker
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.DataStorePathResolver
import com.foreverrafs.superdiary.data.datasource.LocalDataSource
import com.foreverrafs.superdiary.data.datasource.remote.DiaryApi
import com.foreverrafs.superdiary.data.datasource.remote.RemoteDataSource
import com.foreverrafs.superdiary.data.datasource.remote.SupabaseDiaryApi
import com.foreverrafs.superdiary.domain.repository.DataSource
import com.foreverrafs.superdiary.domain.usecase.AddWeeklySummaryUseCase
import com.foreverrafs.superdiary.domain.usecase.CalculateBestStreakUseCase
import com.foreverrafs.superdiary.domain.usecase.CalculateStreakUseCase
import com.foreverrafs.superdiary.domain.usecase.ClearDiariesUseCase
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
import kotlin.time.Clock
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

@OptIn(InternalCoroutinesApi::class)
val useCaseModule = module {
    singleOf(::RemoteDataSource) { bind<DataSource>() }

    // The local datasource will get injected by default
    singleOf(::LocalDataSource) { bind<DataSource>() }

    factory<Clock> { Clock.System }

    factory<DiaryPreference> {
        DiaryPreferenceImpl.getInstance(
            dataStore = PreferenceDataStoreFactory.createWithPath {
                get<DataStorePathResolver>().resolve(
                    "diary_preferences.preferences_pb",
                )
            },
        )
    }
    factoryOf(::DiaryValidatorImpl) { bind<DiaryValidator>() }

    factoryOf(::GetFavoriteDiariesUseCase)
    factoryOf(::SearchDiaryBetweenDatesUseCase)
    factoryOf(::SearchDiaryByEntryUseCase)
    factoryOf(::SearchDiaryByDateUseCase)
    factoryOf(::DeleteDiaryUseCase)
    factoryOf(::UpdateDiaryUseCase)
    factoryOf(::GetLatestEntriesUseCase)
    factoryOf(::ClearDiariesUseCase)
    factoryOf(::CountDiariesUseCase)
    factoryOf(::CalculateStreakUseCase)
    factoryOf(::AddWeeklySummaryUseCase)
    factoryOf(::GetWeeklySummaryUseCase)
    factoryOf(::GetDiaryByIdUseCase)
    factoryOf(::GetAllDiariesUseCase)
    factoryOf(::CalculateBestStreakUseCase)
    factoryOf(::SupabaseDiaryApi) { bind<DiaryApi>() }
}

expect fun platformModule(
    analyticsTracker: AnalyticsTracker,
    aggregateLogger: AggregateLogger,
): Module
