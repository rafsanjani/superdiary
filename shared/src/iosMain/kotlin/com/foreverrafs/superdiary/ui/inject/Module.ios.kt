package com.foreverrafs.superdiary.ui.inject

import com.foreverrafs.superdiary.DarwinDatabaseDriver
import com.foreverrafs.superdiary.diary.DatabaseDriver
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    factory<DatabaseDriver> { DarwinDatabaseDriver() }
}
