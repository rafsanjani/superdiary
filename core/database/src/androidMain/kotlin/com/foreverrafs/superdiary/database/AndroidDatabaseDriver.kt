package com.foreverrafs.superdiary.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

class AndroidDatabaseDriver(private val context: Context) : DatabaseDriver {
    override fun createDriver(): SqlDriver = AndroidSqliteDriver(
        schema = SuperDiaryDatabase.Schema,
        context = context,
        name = "diary.db",
    )
}
