package com.foreverrafs.superdiary.data.datasource

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.foreverrafs.superdiary.data.DatabaseDriver

actual class TestDatabaseDriver : DatabaseDriver {
    override fun createDriver(): SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
}
