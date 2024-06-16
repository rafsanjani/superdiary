package com.foreverrafs.superdiary.ui.di

import com.foreverrafs.superdiary.core.analytics.AnalyticsTracker
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.core.logging.KermitLogger
import org.koin.core.context.startKoin

@Suppress("unused")
object KoinApplication {
    fun initialize(
        analytics: AnalyticsTracker,
    ) = startKoin {
        modules(
            modules = compositeModule(
                analytics = analytics,
                logger = AggregateLogger(listOf(KermitLogger())),
            ),
        )
    }
}
