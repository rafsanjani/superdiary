package com.foreverrafs.superdiary.data.datasource

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import app.cash.sqldelight.driver.native.wrapConnection
import co.touchlab.sqliter.DatabaseConfiguration
import com.foreverrafs.superdiary.database.SuperDiaryDatabase
import com.foreverrafs.superdiary.data.DatabaseDriver

actual class TestDatabaseDriver : DatabaseDriver {
    private var index = 0
    override fun createDriver(): SqlDriver {
        index++
        val schema = SuperDiaryDatabase.Schema
        return NativeSqliteDriver(
            DatabaseConfiguration(
                name = "test-$index.db",
                version = schema.version.toInt(),
                create = { connection ->
                    wrapConnection(connection) { schema.create(it) }
                },
                upgrade = { connection, oldVersion, newVersion ->
                    wrapConnection(connection) {
                        schema.migrate(it, oldVersion.toLong(), newVersion.toLong())
                    }
                },
                inMemory = true,
            ),
        )
    }
}
