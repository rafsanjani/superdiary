package com.foreverrafs.superdiary

import app.cash.sqldelight.db.SqlDriver


interface DatabaseDriver {
    fun createDriver(): SqlDriver
}