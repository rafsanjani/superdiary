package com.foreverrafs.superdiary.diary

import app.cash.sqldelight.db.SqlDriver

interface DatabaseDriver {
    fun createDriver(): SqlDriver
}
