package com.foreverrafs.superdiary.ui.di

import com.foreverrafs.superdiary.core.analytics.AnalyticsTracker
import org.koin.core.context.startKoin

@Suppress("unused")
object KoinApplication {
    fun initialize(
        analytics: AnalyticsTracker,
    ) = startKoin {
        modules(compositeModule(analytics))
    }
}
