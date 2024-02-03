package com.foreverrafs.superdiary.data

import app.cash.sqldelight.db.SqlDriver

fun interface DatabaseDriver {
    fun createDriver(): SqlDriver
}
