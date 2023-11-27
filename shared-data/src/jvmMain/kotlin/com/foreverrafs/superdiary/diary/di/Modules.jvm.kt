package com.foreverrafs.superdiary.diary.di

import com.foreverrafs.superdiary.diary.DatabaseDriver
import com.foreverrafs.superdiary.diary.analytics.Analytics
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual fun platformModule(analytics: Analytics): Module = module {
    factoryOf<DatabaseDriver>(::JVMDatabaseDriver)
}
