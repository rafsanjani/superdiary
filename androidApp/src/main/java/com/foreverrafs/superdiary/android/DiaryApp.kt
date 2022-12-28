package com.foreverrafs.superdiary.android

import android.app.Application
import com.foreverrafs.superdiary.appContext

class DiaryApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // We need to initialize this here for SqlDelight to work
        appContext = applicationContext
    }
}