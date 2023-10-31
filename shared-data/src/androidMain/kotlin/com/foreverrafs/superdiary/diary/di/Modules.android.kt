package com.foreverrafs.superdiary.diary.di

import com.foreverrafs.superdiary.AndroidDatabaseDriver
import com.foreverrafs.superdiary.diary.DatabaseDriver
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    factory<DatabaseDriver> { AndroidDatabaseDriver(get()) }
}
