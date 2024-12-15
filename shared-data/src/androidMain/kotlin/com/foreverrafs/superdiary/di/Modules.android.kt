package com.foreverrafs.superdiary.di

import com.foreverrafs.superdiary.core.analytics.AnalyticsTracker
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(
    analyticsTracker: AnalyticsTracker,
    aggregateLogger: AggregateLogger,
): Module = module {
    single<AnalyticsTracker> { analyticsTracker }
    factory<AggregateLogger> { aggregateLogger }
}
