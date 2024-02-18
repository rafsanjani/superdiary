package com.foreverrafs.superdiary.data.datasource

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import app.cash.sqldelight.driver.native.wrapConnection
import co.touchlab.sqliter.DatabaseConfiguration
import com.benasher44.uuid.uuid4
import com.foreverrafs.superdiary.data.DatabaseDriver
import com.foreverrafs.superdiary.database.SuperDiaryDatabase

actual class TestDatabaseDriver : DatabaseDriver {
    override fun createDriver(): SqlDriver {
        val schema = SuperDiaryDatabase.Schema
        return NativeSqliteDriver(
            DatabaseConfiguration(
                name = uuid4().toString(),
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
