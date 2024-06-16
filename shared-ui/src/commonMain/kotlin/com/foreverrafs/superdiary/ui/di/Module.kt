package com.foreverrafs.superdiary.ui.di

import com.foreverrafs.superdiary.core.analytics.AnalyticsTracker
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.core.utils.utilsModule
import com.foreverrafs.superdiary.data.di.platformModule
import com.foreverrafs.superdiary.data.di.useCaseModule
import com.foreverrafs.superdiary.ui.feature.creatediary.screen.CreateDiaryViewModel
import com.foreverrafs.superdiary.ui.feature.dashboard.DashboardViewModel
import com.foreverrafs.superdiary.ui.feature.details.DetailsViewModel
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
    singleOf(::DetailsViewModel)
}

/** This is the only component that is exposed outside of this module */
fun compositeModule(
    analytics: AnalyticsTracker,
    logger: AggregateLogger,
): List<Module> = listOf(
    utilsModule(),
    useCaseModule(),
    screenModules(),
    platformModule(analyticsTracker = analytics, aggregateLogger = logger),
)
