package com.foreverrafs.superdiary.diary.di

import com.foreverrafs.superdiary.diary.Database
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.datasource.LocalDataSource
import com.foreverrafs.superdiary.diary.generator.DiaryAI
import com.foreverrafs.superdiary.diary.generator.SuperDiaryAI
import com.foreverrafs.superdiary.diary.usecase.AddDiaryUseCase
import com.foreverrafs.superdiary.diary.usecase.DeleteDiaryUseCase
import com.foreverrafs.superdiary.diary.usecase.DeleteMultipleDiariesUseCase
import com.foreverrafs.superdiary.diary.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.diary.usecase.GetFavoriteDiariesUseCase
import com.foreverrafs.superdiary.diary.usecase.SearchDiaryBetweenDatesUseCase
import com.foreverrafs.superdiary.diary.usecase.SearchDiaryByDateUseCase
import com.foreverrafs.superdiary.diary.usecase.SearchDiaryByEntryUseCase
import com.foreverrafs.superdiary.diary.usecase.UpdateDiaryUseCase
import com.foreverrafs.superdiary.diary.utils.DiaryValidator
import kotlinx.datetime.Clock
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun useCaseModule() = module {
    single<DataSource> { LocalDataSource(get()) }
    factory<Clock> { Clock.System }

    singleOf<DiaryAI>(::SuperDiaryAI)
    singleOf(::AddDiaryUseCase)
    singleOf(::GetAllDiariesUseCase)
    singleOf(::GetFavoriteDiariesUseCase)
    singleOf(::SearchDiaryBetweenDatesUseCase)
    singleOf(::SearchDiaryByEntryUseCase)
    singleOf(::SearchDiaryByEntryUseCase)
    singleOf(::SearchDiaryByDateUseCase)
    singleOf(::DeleteDiaryUseCase)
    singleOf(::DeleteMultipleDiariesUseCase)
    singleOf(::UpdateDiaryUseCase)
    singleOf(::Database)
    singleOf(::DiaryValidator)
}
