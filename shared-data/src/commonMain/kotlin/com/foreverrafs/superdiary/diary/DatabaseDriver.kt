package com.foreverrafs.superdiary.diary

import app.cash.sqldelight.db.SqlDriver

fun interface DatabaseDriver {
    fun createDriver(): SqlDriver
}
