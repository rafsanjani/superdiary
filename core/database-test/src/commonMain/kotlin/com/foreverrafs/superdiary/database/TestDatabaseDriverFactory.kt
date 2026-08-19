package com.foreverrafs.superdiary.database

import app.cash.sqldelight.db.SqlDriver

internal expect class TestDatabaseDriverFactory() : DatabaseDriverFactory {
    override fun createSqlDriver(): SqlDriver
}
