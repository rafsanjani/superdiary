package com.foreverrafs.superdiary.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

actual class TestDatabaseDriver : DatabaseDriver {
    override fun createDriver(): SqlDriver =
        JdbcSqliteDriver(JdbcSqliteDriver.Companion.IN_MEMORY)
}
