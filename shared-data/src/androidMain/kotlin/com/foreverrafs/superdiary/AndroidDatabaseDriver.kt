package com.foreverrafs.superdiary

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.foreverrafs.superdiary.database.SuperDiaryDatabase
import com.foreverrafs.superdiary.diary.DatabaseDriver

class AndroidDatabaseDriver(private val context: Context) : DatabaseDriver {
    override fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = SuperDiaryDatabase.Schema,
            context = context,
            name = "diary.db",
        )
    }
}
