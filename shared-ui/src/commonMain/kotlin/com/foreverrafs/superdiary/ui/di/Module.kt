package com.foreverrafs.superdiary.ui.di

import com.foreverrafs.auth.di.authModule
import com.foreverrafs.superdiary.ai.di.diaryAiModule
import com.foreverrafs.superdiary.auth.di.diaryAuthModule
import com.foreverrafs.superdiary.auth.login.BiometricLoginScreenViewModel
import com.foreverrafs.superdiary.auth.login.LoginScreenViewModel
import com.foreverrafs.superdiary.auth.register.DeeplinkContainer
import com.foreverrafs.superdiary.auth.register.RegisterScreenViewModel
import com.foreverrafs.superdiary.auth.reset.PasswordResetViewModel
import com.foreverrafs.superdiary.common.utils.di.utilsModule
import com.foreverrafs.superdiary.core.analytics.AnalyticsTracker
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.core.permission.LocationPermissionManager
import com.foreverrafs.superdiary.core.permission.di.permissionsModule
import com.foreverrafs.superdiary.core.sync.di.syncModule
import com.foreverrafs.superdiary.dashboard.di.dashboardModule
import com.foreverrafs.superdiary.di.platformModule
import com.foreverrafs.superdiary.di.useCaseModule
import com.foreverrafs.superdiary.domain.usecase.GetDiaryByIdUseCase
import com.foreverrafs.superdiary.list.di.diaryListModule
import com.foreverrafs.superdiary.profile.di.profileModule
import com.foreverrafs.superdiary.ui.AppViewModel
import com.foreverrafs.superdiary.ui.feature.diarychat.DiaryChatViewModel
import com.foreverrafs.superdiary.ui.feature.favorites.FavoriteViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val screensModule: Module = module {
    factoryOf(::LocationPermissionManager)
    singleOf(::DeeplinkContainer)

    factoryOf(::GetDiaryByIdUseCase)
    viewModelOf(::FavoriteViewModel)
    viewModelOf(::DiaryChatViewModel)
    viewModelOf(::RegisterScreenViewModel)
    viewModelOf(::LoginScreenViewModel)
    viewModelOf(::AppViewModel)
    viewModelOf(::PasswordResetViewModel)
    viewModelOf(::BiometricLoginScreenViewModel)
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
    useCaseModule,
    screensModule,
    platformModule(analyticsTracker = analytics, aggregateLogger = logger),
    authModule(),
    diaryAiModule,
    profileModule,
    diaryListModule,
    dashboardModule,
    syncModule,
    diaryAuthModule,
)
