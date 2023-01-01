package com.foreverrafs.superdiary

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import db.KmpSuperDiaryDB

class AndroidDatabaseDriver(private val context: Context) : DatabaseDriver {
    override fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = KmpSuperDiaryDB.Schema,
            context = context,
            name = "diary.db",
        )
    }
}