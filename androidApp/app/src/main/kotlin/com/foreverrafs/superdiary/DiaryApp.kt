package com.foreverrafs.superdiary

import android.app.Application
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.core.logging.KermitLogger
import com.foreverrafs.superdiary.core.logging.SentryLogger
import com.foreverrafs.superdiary.data.androidFilesDirectory
import com.foreverrafs.superdiary.ui.di.compositeModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class DiaryApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeKoin()
    }

    private fun initializeKoin() {
        startKoin {
            androidContext(this@DiaryApp)

            androidFilesDirectory = filesDir

            modules(
                modules = compositeModule(
                    analytics = AndroidAnalytics(),
                    logger = AggregateLogger(
                        loggers = listOf(
                            SentryLogger(),
                            KermitLogger(),
                        ),
                    ),
                ),
            )
        }
    }
}
