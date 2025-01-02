package com.foreverrafs.superdiary.di

import com.foreverrafs.superdiary.core.analytics.AnalyticsTracker
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.DataStorePathResolver
import com.foreverrafs.superdiary.data.JVMDataStorePathResolver
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual fun platformModule(
    analyticsTracker: AnalyticsTracker,
    aggregateLogger: AggregateLogger,
): Module = module {
    factory<AnalyticsTracker> { analyticsTracker }
    factory<AggregateLogger> { aggregateLogger }
    factoryOf(::JVMDataStorePathResolver) bind DataStorePathResolver::class
}
