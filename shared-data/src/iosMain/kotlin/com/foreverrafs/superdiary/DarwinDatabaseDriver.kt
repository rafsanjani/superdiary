package com.foreverrafs.superdiary

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.foreverrafs.superdiary.database.SuperDiaryDatabase
import com.foreverrafs.superdiary.diary.DatabaseDriver

class DarwinDatabaseDriver : DatabaseDriver {
    override fun createDriver(): SqlDriver {
        return NativeSqliteDriver(SuperDiaryDatabase.Schema, "diary.db")
    }
}
