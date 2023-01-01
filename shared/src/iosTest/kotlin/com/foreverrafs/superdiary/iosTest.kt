package com.foreverrafs.superdiary

import co.touchlab.sqliter.DatabaseConfiguration
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import com.squareup.sqldelight.drivers.native.wrapConnection
import db.KmpSuperDiaryDB
import kotlin.test.Test

class IosTest{
    @Test
    fun test(){

    }
}
var index = 0;
actual fun createInMemorySqlDriver(): SqlDriver {
    val schema = KmpSuperDiaryDB.Schema

    return NativeSqliteDriver(
        DatabaseConfiguration(
            name = "test-$index.db",
            version = schema.version,
            create = { connection ->
                wrapConnection(connection) { schema.create(it) }
            },
            upgrade = { connection, oldVersion, newVersion ->
                wrapConnection(connection) {
                    schema.migrate(it, oldVersion, newVersion)
                }
            },
            inMemory = true
        )
    )
}