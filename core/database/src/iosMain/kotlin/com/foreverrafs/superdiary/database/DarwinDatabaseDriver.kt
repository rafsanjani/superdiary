package com.foreverrafs.superdiary.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

class DarwinDatabaseDriver : DatabaseDriver {
    override fun createDriver(): SqlDriver =
        NativeSqliteDriver(SuperDiaryDatabase.Schema, toString())
}
