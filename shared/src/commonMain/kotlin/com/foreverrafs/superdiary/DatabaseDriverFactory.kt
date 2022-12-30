package com.foreverrafs.superdiary

import com.squareup.sqldelight.db.SqlDriver


expect class DatabaseDriverFactory {
    internal fun createDriver(): SqlDriver
}