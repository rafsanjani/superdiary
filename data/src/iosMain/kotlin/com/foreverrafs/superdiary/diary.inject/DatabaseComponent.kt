package com.foreverrafs.superdiary.diary.inject

import com.foreverrafs.superdiary.DarwinDatabaseDriver
import com.foreverrafs.superdiary.diary.DatabaseDriver
import me.tatarka.inject.annotations.Provides

actual interface DatabaseComponent {
    @Provides
    fun provideDatabaseDriver(): DatabaseDriver = DarwinDatabaseDriver()
}
