package com.foreverrafs.superdiary.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

internal actual class TestDatabaseDriverFactory : DatabaseDriverFactory {
    actual override fun createSqlDriver(): SqlDriver {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        SuperDiaryDatabase.Schema.create(driver)
        return driver
    }
}
