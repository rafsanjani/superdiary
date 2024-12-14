package com.foreverrafs.superdiary.database.di

import com.foreverrafs.superdiary.database.Database
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun databaseModule(): Module = module {
    includes(databaseDriverModule())
    singleOf(::Database)
}

expect fun databaseDriverModule(): Module
