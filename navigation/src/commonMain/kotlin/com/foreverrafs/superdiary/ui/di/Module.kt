package com.foreverrafs.superdiary.ui.di

import com.foreverrafs.auth.di.authModule
import com.foreverrafs.superdiary.ai.di.diaryAiModule
import com.foreverrafs.superdiary.auth.di.diaryAuthModule
import com.foreverrafs.superdiary.chat.di.diaryChatModule
import com.foreverrafs.superdiary.common.utils.di.utilsModule
import com.foreverrafs.superdiary.core.analytics.AnalyticsTracker
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.core.permission.di.permissionsModule
import com.foreverrafs.superdiary.creatediary.di.createDiaryModule
import com.foreverrafs.superdiary.dashboard.di.dashboardModule
import com.foreverrafs.superdiary.database.di.databaseModule
import com.foreverrafs.superdiary.di.platformModule
import com.foreverrafs.superdiary.di.useCaseModule
import com.foreverrafs.superdiary.favorite.di.favoriteModule
import com.foreverrafs.superdiary.list.di.diaryListModule
import com.foreverrafs.superdiary.profile.di.profileModule
import com.foreverrafs.superdiary.ui.AppViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val appModule: Module = module {
    viewModelOf(::AppViewModel)
}

expect fun savedStateModule(): Module

/** This is the only component that is exposed outside of this module */
fun compositeModule(
    analytics: AnalyticsTracker,
    logger: AggregateLogger,
): List<Module> = listOf(
    utilsModule,
    savedStateModule(),
    permissionsModule(),
    databaseModule(),
    useCaseModule,
    appModule,
    platformModule(analyticsTracker = analytics, aggregateLogger = logger),
    authModule(),
    diaryAiModule,
    createDiaryModule,
    profileModule,
    diaryListModule,
    dashboardModule,
    diaryAuthModule,
    favoriteModule,
    diaryChatModule,
)
