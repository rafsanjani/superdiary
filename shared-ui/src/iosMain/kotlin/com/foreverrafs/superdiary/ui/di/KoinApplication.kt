package com.foreverrafs.superdiary.ui.di

import com.foreverrafs.superdiary.data.analytics.Analytics
import com.foreverrafs.superdiary.data.di.platformModule
import com.foreverrafs.superdiary.data.di.useCaseModule
import org.koin.core.context.startKoin

@Suppress("unused")
object KoinApplication {
    fun initialize(
        analytics: Analytics,
    ) = startKoin {
        modules(useCaseModule(), screenModules(), platformModule(analytics))
    }
}
