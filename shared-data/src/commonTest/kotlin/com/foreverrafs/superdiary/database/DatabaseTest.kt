package com.foreverrafs.superdiary.database

import com.foreverrafs.superdiary.database.model.DiaryDb
import kotlin.test.Test
import kotlin.test.assertFailsWith

class DatabaseTest {
    private val database = Database(testSuperDiaryDatabase)

    @Test
    fun `update propagates invalid diary data errors`() {
        val diary = DiaryDb(
            id = 1,
            entry = "Entry",
            location = "invalid,location",
        )

        assertFailsWith<IllegalArgumentException> {
            database.update(diary)
        }
    }
}
