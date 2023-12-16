package com.foreverrafs.superdiary.ui.di

import com.foreverrafs.superdiary.diary.analytics.Analytics
import com.foreverrafs.superdiary.diary.di.platformModule
import com.foreverrafs.superdiary.diary.di.useCaseModule
import com.foreverrafs.superdiary.ui.feature.creatediary.screen.CreateDiaryViewModel
import com.foreverrafs.superdiary.ui.feature.dashboard.DashboardViewModel
import com.foreverrafs.superdiary.ui.feature.diarychat.DiaryChatViewModel
import com.foreverrafs.superdiary.ui.feature.diarylist.model.DiaryListViewModel
import com.foreverrafs.superdiary.ui.feature.favorites.model.FavoriteViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal fun screenModules(): Module = module {
    singleOf(::DiaryListViewModel)
    singleOf(::CreateDiaryViewModel)
    singleOf(::FavoriteViewModel)
    singleOf(::DashboardViewModel)
    singleOf(::DiaryChatViewModel)
}

fun appModule(analytics: Analytics) =
    listOf(useCaseModule(), screenModules(), platformModule(analytics))
