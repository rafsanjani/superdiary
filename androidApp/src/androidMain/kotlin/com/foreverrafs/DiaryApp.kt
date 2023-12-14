package com.foreverrafs

import android.app.Application
import com.foreverrafs.superdiary.analytics.AndroidAnalytics
import com.foreverrafs.superdiary.ui.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.time.LocalDate

class DiaryApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            val a = LocalDate.now().atStartOfDay()
            androidContext(this@DiaryApp)
            modules(
                modules = appModule(analytics = AndroidAnalytics()),
            )
        }
    }
}
