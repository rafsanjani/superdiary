package com.foreverrafs.superdiary.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

class AndroidDatabaseDriverFactory(private val context: Context) : DatabaseDriverFactory {
    override fun createSqlDriver(): SqlDriver = AndroidSqliteDriver(
        schema = SuperDiaryDatabase.Schema,
        context = context,
        name = "diary.db",
    )
}
