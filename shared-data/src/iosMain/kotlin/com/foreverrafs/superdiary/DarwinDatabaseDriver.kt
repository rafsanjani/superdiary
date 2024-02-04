package com.foreverrafs.superdiary

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.foreverrafs.superdiary.data.DatabaseDriver
import com.foreverrafs.superdiary.database.SuperDiaryDatabase
import platform.Foundation.NSUUID

class DarwinDatabaseDriver : DatabaseDriver {
    override fun createDriver(): SqlDriver {
        return NativeSqliteDriver(SuperDiaryDatabase.Schema, NSUUID.toString())
    }
}
