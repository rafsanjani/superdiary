package com.foreverrafs.superdiary.database.di

import com.foreverrafs.superdiary.database.DarwinDatabaseDriver
import com.foreverrafs.superdiary.database.DatabaseDriver
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual fun databaseDriverModule(): Module = module {
    singleOf(::DarwinDatabaseDriver) { bind<DatabaseDriver>() }
}
