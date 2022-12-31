package com.foreverrafs.superdiary

import com.foreverrafs.superdiary.diary.model.Diary
import com.squareup.sqldelight.db.SqlDriver
import kotlin.test.Test

expect fun createInMemorySqlDriver(): SqlDriver

class CommonTest{
    @Test
    fun testDatabaseCreation() {
        val databaseDriver: DatabaseDriver = object : DatabaseDriver {
            override fun createDriver(): SqlDriver = createInMemorySqlDriver()
        }

        val database = Database(databaseDriver)
        database.addDiary(Diary(entry = "", date = ""))
    }
}