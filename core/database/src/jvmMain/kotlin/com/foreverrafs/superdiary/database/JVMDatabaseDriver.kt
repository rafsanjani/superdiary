package com.foreverrafs.superdiary.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

class JVMDatabaseDriver : DatabaseDriver {
    override fun createDriver(): SqlDriver = JdbcSqliteDriver(
        url = "jdbc:sqlite:diary.db",
    )
}
