package com.foreverrafs.superdiary.ui.di

import com.foreverrafs.superdiary.diary.analytics.Analytics
import com.foreverrafs.superdiary.diary.di.platformModule
import com.foreverrafs.superdiary.diary.di.useCaseModule
import org.koin.core.context.startKoin

@Suppress("unused")
object KoinApplication {
    fun initialize(
        analytics: Analytics,
    ) = startKoin {
        modules(useCaseModule(), screenModules(), platformModule(analytics))
    }
}
