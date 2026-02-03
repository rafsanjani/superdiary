package com.foreverrafs.superdiary.database.di

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import com.foreverrafs.superdiary.database.Database
import com.foreverrafs.superdiary.database.DatabaseDriverFactory
import com.foreverrafs.superdiary.database.SuperDiaryDatabase
import com.foreverrafs.superdiary.database.instantAdapter
import com.foreverrafs.superdiary.database.model.DiaryChatRoleDb
import com.foreverrafs.superdiary.database.model.locationAdapter
import db.Chat
import db.Diary
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun databaseModule(): Module = module {
    includes(databaseDriverModule())

    single<SqlDriver> {
        val driverFactory = get<DatabaseDriverFactory>()
        driverFactory.createSqlDriver()
    }

    single {
        SuperDiaryDatabase(
            driver = get(),
            diaryAdapter = Diary.Adapter(
                dateAdapter = instantAdapter,
                locationAdapter = locationAdapter,
                updated_atAdapter = instantAdapter
            ),
            chatAdapter = Chat.Adapter(
                dateAdapter = instantAdapter,
                roleAdapter = EnumColumnAdapter<DiaryChatRoleDb>(),
            ),
        )
    }
    singleOf(::Database)
}

expect fun databaseDriverModule(): Module
