package com.foreverrafs.superdiary.diary.di

import com.foreverrafs.superdiary.DarwinDatabaseDriver
import com.foreverrafs.superdiary.diary.DatabaseDriver
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    factoryOf<DatabaseDriver>(::DarwinDatabaseDriver)
}
