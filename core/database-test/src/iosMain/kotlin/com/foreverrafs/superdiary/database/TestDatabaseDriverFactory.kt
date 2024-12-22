package com.foreverrafs.superdiary.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import app.cash.sqldelight.driver.native.wrapConnection
import co.touchlab.sqliter.DatabaseConfiguration
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal actual class TestDatabaseDriverFactory : DatabaseDriverFactory {
    @OptIn(ExperimentalUuidApi::class)
    override fun createSqlDriver(): SqlDriver {
        val schema = SuperDiaryDatabase.Schema
        return NativeSqliteDriver(
            DatabaseConfiguration(
                name = Uuid.random().toString(),
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
