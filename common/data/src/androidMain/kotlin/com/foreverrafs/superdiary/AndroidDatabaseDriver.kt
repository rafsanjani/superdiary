package com.foreverrafs.superdiary

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.foreverrafs.superdiary.diary.DatabaseDriver
import db.KmpSuperDiaryDB

class AndroidDatabaseDriver(private val context: Context) : DatabaseDriver {
    override fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = KmpSuperDiaryDB.Schema,
            context = context,
            name = "diary.db",
        )
    }
}
