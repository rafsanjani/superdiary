package com.superdiary

import android.app.Application
import com.foreverrafs.superdiary.analytics.AndroidAnalytics
import com.foreverrafs.superdiary.ui.di.compositeModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class DiaryApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@DiaryApp)
            modules(
                modules = compositeModule(analytics = AndroidAnalytics()),
            )
        }
    }
}
