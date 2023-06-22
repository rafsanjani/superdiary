package com.foreverrafs.superdiary

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.foreverrafs.superdiary.diary.DatabaseDriver
import db.KmpSuperDiaryDB

class DarwinDatabaseDriver : DatabaseDriver {
    override fun createDriver(): SqlDriver {
        return NativeSqliteDriver(KmpSuperDiaryDB.Schema, "diary.db")
    }
}
