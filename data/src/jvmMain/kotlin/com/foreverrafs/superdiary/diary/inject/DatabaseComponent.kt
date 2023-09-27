package com.foreverrafs.superdiary.diary.inject

import com.foreverrafs.superdiary.diary.DatabaseDriver
import me.tatarka.inject.annotations.Provides

@AppScope
actual interface DatabaseComponent {
    @Provides
    fun provideDatabaseDriver(): DatabaseDriver = JVMDatabaseDriver()
}
