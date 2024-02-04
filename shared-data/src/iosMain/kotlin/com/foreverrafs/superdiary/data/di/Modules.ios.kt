package com.foreverrafs.superdiary.data.di

import com.foreverrafs.superdiary.DarwinDatabaseDriver
import com.foreverrafs.superdiary.data.DatabaseDriver
import com.foreverrafs.superdiary.data.analytics.Analytics
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual fun platformModule(analytics: Analytics): Module = module {
    singleOf<DatabaseDriver>(::DarwinDatabaseDriver)
    single<Analytics> { analytics }
}
