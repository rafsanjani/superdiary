package com.foreverrafs.superdiary.database.di

import com.foreverrafs.superdiary.database.DarwinDatabaseDriverFactory
import com.foreverrafs.superdiary.database.DatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual fun databaseDriverModule(): Module = module {
    singleOf(::DarwinDatabaseDriverFactory) { bind<DatabaseDriverFactory>() }
}
