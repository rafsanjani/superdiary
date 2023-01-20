package com.foreverrafs.superdiary

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import db.KmpSuperDiaryDB

class DarwinDatabaseDriver : DatabaseDriver {
    override fun createDriver(): SqlDriver {
        return NativeSqliteDriver(KmpSuperDiaryDB.Schema, "diary.db")
    }
}