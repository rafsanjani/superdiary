package com.foreverrafs.superdiary

import com.squareup.sqldelight.db.SqlDriver


interface DatabaseDriver {
    fun createDriver(): SqlDriver
}