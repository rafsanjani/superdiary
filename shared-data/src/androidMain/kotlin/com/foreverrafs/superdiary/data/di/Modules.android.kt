package com.foreverrafs.superdiary.data.di

import com.foreverrafs.superdiary.AndroidDatabaseDriver
import com.foreverrafs.superdiary.core.analytics.AnalyticsTracker
import com.foreverrafs.superdiary.data.DatabaseDriver
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(analyticsTracker: AnalyticsTracker): Module = module {
    single<AnalyticsTracker> { analyticsTracker }
    factory<DatabaseDriver> { AndroidDatabaseDriver(get()) }
}
