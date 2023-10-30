package com.foreverrafs.superdiary.ui.di

import com.foreverrafs.superdiary.diary.DatabaseDriver
import com.foreverrafs.superdiary.diary.di.JVMDatabaseDriver
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    factoryOf<DatabaseDriver>(::JVMDatabaseDriver)
}
