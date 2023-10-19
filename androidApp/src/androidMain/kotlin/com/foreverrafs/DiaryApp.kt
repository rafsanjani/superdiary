package com.foreverrafs

import android.app.Application
import com.foreverrafs.superdiary.ui.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class DiaryApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@DiaryApp)
            modules(appModule())
        }
    }
}
