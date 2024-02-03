package com.foreverrafs.superdiary.data.di

import com.foreverrafs.superdiary.AndroidDatabaseDriver
import com.foreverrafs.superdiary.data.DatabaseDriver
import com.foreverrafs.superdiary.data.analytics.Analytics
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(analytics: Analytics): Module = module {
    single<Analytics> { analytics }
    factory<DatabaseDriver> { AndroidDatabaseDriver(get()) }
}
