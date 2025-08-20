package com.foreverrafs.superdiary.di

import com.foreverrafs.superdiary.core.analytics.AnalyticsTracker
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.AndroidDataStorePathResolver
import com.foreverrafs.superdiary.data.DataStorePathResolver
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual fun platformModule(
    analyticsTracker: AnalyticsTracker,
    aggregateLogger: AggregateLogger,
): Module = module {
    single<AnalyticsTracker> { analyticsTracker }
    factory<AggregateLogger> { aggregateLogger }
    factoryOf(::AndroidDataStorePathResolver) bind DataStorePathResolver::class
}
