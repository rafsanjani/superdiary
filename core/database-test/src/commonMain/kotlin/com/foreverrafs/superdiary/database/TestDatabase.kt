package com.foreverrafs.superdiary.database

import app.cash.sqldelight.EnumColumnAdapter
import com.foreverrafs.superdiary.database.model.DiaryChatRoleDb
import com.foreverrafs.superdiary.database.model.locationAdapter
import db.Chat
import db.Diary

val testSuperDiaryDatabase: SuperDiaryDatabase
    get() {
        val driver = TestDatabaseDriverFactory().createSqlDriver()

        val db = SuperDiaryDatabase(
            driver = driver,
            diaryAdapter = Diary.Adapter(
                dateAdapter = instantAdapter,
                locationAdapter = locationAdapter,
                updated_atAdapter = instantAdapter,
            ),
            chatAdapter = Chat.Adapter(
                dateAdapter = instantAdapter,
                roleAdapter = EnumColumnAdapter<DiaryChatRoleDb>(),
            ),
        )

        return db
    }
