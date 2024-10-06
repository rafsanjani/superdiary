package com.foreverrafs.superdiary

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.foreverrafs.superdiary.data.DatabaseDriver
import com.foreverrafs.superdiary.database.SuperDiaryDatabase

class AndroidDatabaseDriver(private val context: Context) : DatabaseDriver {
    override fun createDriver(): SqlDriver = AndroidSqliteDriver(
        schema = SuperDiaryDatabase.Schema,
        context = context,
        name = "diary.db",
    )
}
