package com.foreverrafs.superdiary.di

import com.foreverrafs.superdiary.DarwinDatabaseDriver
import com.foreverrafs.superdiary.core.analytics.AnalyticsTracker
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.DatabaseDriver
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual fun platformModule(
    analyticsTracker: AnalyticsTracker,
    aggregateLogger: AggregateLogger,
): Module = module {
    singleOf<DatabaseDriver>(::DarwinDatabaseDriver)
    single<AnalyticsTracker> { analyticsTracker }
    single<AggregateLogger> { aggregateLogger }
}
