package com.foreverrafs.superdiary.data.di

import com.foreverrafs.superdiary.data.DatabaseDriver
import com.foreverrafs.superdiary.data.JVMDatabaseDriver
import com.foreverrafs.superdiary.data.analytics.Analytics
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual fun platformModule(analytics: Analytics): Module = module {
    factoryOf<DatabaseDriver>(::JVMDatabaseDriver)
    factory<Analytics> { analytics }
}
