package com.foreverrafs.superdiary.diary.di

import com.foreverrafs.superdiary.DarwinDatabaseDriver
import com.foreverrafs.superdiary.diary.DatabaseDriver
import com.foreverrafs.superdiary.diary.analytics.Analytics
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual fun platformModule(analytics: Analytics): Module = module {
    singleOf<DatabaseDriver>(::DarwinDatabaseDriver)
    single<Analytics> { analytics }
}
