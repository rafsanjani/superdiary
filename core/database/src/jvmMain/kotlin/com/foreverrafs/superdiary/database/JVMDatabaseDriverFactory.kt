package com.foreverrafs.superdiary.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

class JVMDatabaseDriverFactory : DatabaseDriverFactory {
    override fun createSqlDriver(): SqlDriver = JdbcSqliteDriver(
        url = "jdbc:sqlite:diary.db",
    )
}
