package com.foreverrafs.superdiary.ui.di

import com.foreverrafs.superdiary.DarwinDatabaseDriver
import com.foreverrafs.superdiary.diary.DatabaseDriver
import com.foreverrafs.superdiary.diary.di.useCaseModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    factory<DatabaseDriver> { DarwinDatabaseDriver() }
}

@Suppress("unused")
object KoinApplication {
    fun initialize() = startKoin {
        modules(useCaseModule(), screenModules(), platformModule())
    }
}
