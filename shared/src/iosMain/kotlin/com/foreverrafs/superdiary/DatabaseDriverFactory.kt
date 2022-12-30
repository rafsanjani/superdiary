package com.foreverrafs.superdiary

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import db.KmpSuperDiaryDB

actual class DatabaseDriverFactory {
    internal actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(KmpSuperDiaryDB.Schema, "diary.db")
    }
}