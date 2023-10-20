package com.foreverrafs.superdiary.diary.di

import com.foreverrafs.superdiary.diary.Database
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.datasource.LocalDataSource
import com.foreverrafs.superdiary.diary.usecase.AddDiaryUseCase
import com.foreverrafs.superdiary.diary.usecase.DeleteDiaryUseCase
import com.foreverrafs.superdiary.diary.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.diary.usecase.SearchDiaryBetweenDatesUseCase
import com.foreverrafs.superdiary.diary.utils.DiaryValidator
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun useCaseModule() = module {
    factory<DataSource> { LocalDataSource(get()) }
    singleOf(::AddDiaryUseCase)
    singleOf(::GetAllDiariesUseCase)
    singleOf(::SearchDiaryBetweenDatesUseCase)
    singleOf(::DeleteDiaryUseCase)
    singleOf(::Database)
    singleOf(::DiaryValidator)
}
