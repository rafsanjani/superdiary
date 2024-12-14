package com.foreverrafs.superdiary.database

import app.cash.sqldelight.db.SqlDriver

// Can't use expect/actual because AndroidDatabaseDriver requires a  context as a ctor param
interface DatabaseDriver {
    fun createDriver(): SqlDriver
}
