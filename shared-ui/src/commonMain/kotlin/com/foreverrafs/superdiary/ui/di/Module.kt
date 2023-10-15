package com.foreverrafs.superdiary.ui.di

import com.foreverrafs.superdiary.diary.inject.useCaseModule
import com.foreverrafs.superdiary.diary.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.ui.feature.creatediary.CreateDiaryPageModel
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryListPageModel
import org.koin.core.module.Module
import org.koin.dsl.module

internal fun screenModules(): Module = module {
    single { GetAllDiariesUseCase(get()) }
    single { DiaryListPageModel(get()) }
    single { CreateDiaryPageModel() }
}

expect fun platformModule(): Module
fun appModule() = listOf(useCaseModule(), screenModules(), platformModule())
