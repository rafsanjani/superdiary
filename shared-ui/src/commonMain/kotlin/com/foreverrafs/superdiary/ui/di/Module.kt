package com.foreverrafs.superdiary.ui.di

import com.foreverrafs.superdiary.diary.inject.useCaseModule
import com.foreverrafs.superdiary.ui.feature.creatediary.CreateDiaryScreenModel
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryListScreenModel
import org.koin.core.module.Module
import org.koin.dsl.module

internal fun screenModules(): Module = module {
    factory { DiaryListScreenModel(get()) }
    factory { CreateDiaryScreenModel(get()) }
}

expect fun platformModule(): Module
fun appModule() = listOf(useCaseModule(), screenModules(), platformModule())
