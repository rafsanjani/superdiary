package com.foreverrafs.superdiary

import android.app.Application
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.core.logging.KermitLogger
import com.foreverrafs.superdiary.core.logging.SentryLogger
import com.foreverrafs.superdiary.ui.di.compositeModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.module.Module

abstract class BaseDiaryApp : Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()
        initializeKoin()
    }

    open fun koinModules(): List<Module> = compositeModule(
        analytics = AndroidAnalytics(),
        logger = AggregateLogger(
            loggers = listOf(
                SentryLogger(),
                KermitLogger(),
            ),
        ),
    )

    private fun initializeKoin() {
        startKoin {
            androidContext(this@BaseDiaryApp)

            modules(
                modules = koinModules(),
            )
        }
    }
}
