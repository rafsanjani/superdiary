package com.foreverrafs.superdiary.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

internal actual class TestDatabaseDriverFactory : DatabaseDriverFactory {
    override fun createSqlDriver(): SqlDriver =
        JdbcSqliteDriver(JdbcSqliteDriver.Companion.IN_MEMORY)
}
