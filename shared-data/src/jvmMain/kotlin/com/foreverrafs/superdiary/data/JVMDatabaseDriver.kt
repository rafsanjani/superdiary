package com.foreverrafs.superdiary.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

class JVMDatabaseDriver : DatabaseDriver {
    override fun createDriver(): SqlDriver {
        return JdbcSqliteDriver(
            url = "jdbc:sqlite:diary.db",
        )
    }
}
