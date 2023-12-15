package com.foreverrafs.superdiary.ui.di

import com.foreverrafs.superdiary.diary.analytics.Analytics
import com.foreverrafs.superdiary.diary.di.platformModule
import com.foreverrafs.superdiary.diary.di.useCaseModule
import com.foreverrafs.superdiary.ui.feature.creatediary.screen.CreateDiaryScreenModel
import com.foreverrafs.superdiary.ui.feature.dashboard.DashboardScreenModel
import com.foreverrafs.superdiary.ui.feature.diarychat.DiaryChatScreenModel
import com.foreverrafs.superdiary.ui.feature.diarylist.model.DiaryListScreenModel
import com.foreverrafs.superdiary.ui.feature.favorites.model.FavoriteScreenModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal fun screenModules(): Module = module {
    singleOf(::DiaryListScreenModel)
    singleOf(::CreateDiaryScreenModel)
    singleOf(::FavoriteScreenModel)
    singleOf(::DashboardScreenModel)
    singleOf(::DiaryChatScreenModel)
}

fun appModule(analytics: Analytics) =
    listOf(useCaseModule(), screenModules(), platformModule(analytics))
