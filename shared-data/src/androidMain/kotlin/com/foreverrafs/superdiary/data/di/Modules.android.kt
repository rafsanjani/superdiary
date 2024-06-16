package com.foreverrafs.superdiary.data.di

import com.foreverrafs.superdiary.AndroidDatabaseDriver
import com.foreverrafs.superdiary.core.analytics.AnalyticsTracker
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.DatabaseDriver
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual fun platformModule(
    analyticsTracker: AnalyticsTracker,
    aggregateLogger: AggregateLogger,
): Module = module {
    single<AnalyticsTracker> { analyticsTracker }
    factoryOf(::AndroidDatabaseDriver) { bind<DatabaseDriver>() }
    factory<AggregateLogger> { aggregateLogger }
}
