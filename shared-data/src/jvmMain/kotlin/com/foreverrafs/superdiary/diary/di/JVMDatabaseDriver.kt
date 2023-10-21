package com.foreverrafs.superdiary.diary.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.foreverrafs.superdiary.diary.DatabaseDriver

class JVMDatabaseDriver : DatabaseDriver {
    override fun createDriver(): SqlDriver {
        return JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    }
}
