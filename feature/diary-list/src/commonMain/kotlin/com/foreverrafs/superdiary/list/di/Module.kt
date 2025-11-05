package com.foreverrafs.superdiary.list.di

import com.foreverrafs.superdiary.list.data.DiaryListRepositoryImpl
import com.foreverrafs.superdiary.list.domain.repository.DiaryListRepository
import com.foreverrafs.superdiary.list.domain.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.list.domain.usecase.GetDiaryByIdUseCase
import com.foreverrafs.superdiary.list.presentation.detail.DetailsViewModel
import com.foreverrafs.superdiary.list.presentation.list.DiaryListViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val diaryListModule = module {
    viewModelOf(::DiaryListViewModel)
    singleOf(::GetAllDiariesUseCase)
    factoryOf(::DiaryListRepositoryImpl) { bind<DiaryListRepository>() }
    factoryOf(::GetAllDiariesUseCase)
    factoryOf(::GetDiaryByIdUseCase)
    factoryOf(::DetailsViewModel)
}
