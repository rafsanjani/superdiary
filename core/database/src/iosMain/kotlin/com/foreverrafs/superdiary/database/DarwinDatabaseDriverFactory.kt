package com.foreverrafs.superdiary.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

class DarwinDatabaseDriverFactory : DatabaseDriverFactory {
    override fun createSqlDriver(): SqlDriver =
        NativeSqliteDriver(SuperDiaryDatabase.Schema, toString())
}
