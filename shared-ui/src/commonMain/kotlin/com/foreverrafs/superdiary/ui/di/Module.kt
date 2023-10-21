package com.foreverrafs.superdiary.ui.di

import com.foreverrafs.superdiary.diary.di.useCaseModule
import com.foreverrafs.superdiary.ui.feature.creatediary.CreateDiaryScreenModel
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryListScreenModel
import org.koin.core.module.Module
import org.koin.dsl.module

internal fun screenModules(): Module = module {
    factory {
        DiaryListScreenModel(
            getAllDiariesUseCase = get(),
            searchDiaryByEntryUseCase = get(),
            searchDiaryByDateUseCase = get(),
        )
    }
    factory {
        CreateDiaryScreenModel(
            addDiaryUseCase = get(),
        )
    }
}

expect fun platformModule(): Module
fun appModule() = listOf(useCaseModule(), screenModules(), platformModule())
