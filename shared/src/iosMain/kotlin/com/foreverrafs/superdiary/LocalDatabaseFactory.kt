package com.foreverrafs.superdiary

import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import db.KmpSuperDiaryDB

actual class LocalDatabaseFactory {
    actual companion object {
        actual fun getSuperDiaryDB(): KmpSuperDiaryDB {
            val driver = NativeSqliteDriver(schema = KmpSuperDiaryDB.Schema, name = "diary.db")
            return KmpSuperDiaryDB(driver)
        }
    }
}