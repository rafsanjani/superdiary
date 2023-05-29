package com.foreverrafs.superdiary.diary.inject

import android.app.Application
import com.foreverrafs.superdiary.AndroidDatabaseDriver
import com.foreverrafs.superdiary.diary.DatabaseDriver
import me.tatarka.inject.annotations.Provides

actual interface DatabaseComponent {
    @Provides
    fun provideDatabaseDriver(application: Application): DatabaseDriver =
        AndroidDatabaseDriver(application)
}