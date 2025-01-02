package com.foreverrafs.superdiary

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.foreverrafs.auth.AndroidContextProvider
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.core.logging.KermitLogger
import com.foreverrafs.superdiary.core.logging.SentryLogger
import com.foreverrafs.superdiary.ui.di.compositeModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

class DiaryApp : Application(), KoinComponent {
    private val androidContextProvider: AndroidContextProvider by inject()
    override fun onCreate() {
        super.onCreate()
        initializeKoin()
        registerAndroidContextProviderCallbacks()
    }

    private fun registerAndroidContextProviderCallbacks() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                androidContextProvider.setContext(activity)
            }

            override fun onActivityStarted(activity: Activity) {
                androidContextProvider.setContext(activity)
            }

            // Sometimes activities will cycle between paused and resumed states
            override fun onActivityResumed(activity: Activity) {
                androidContextProvider.setContext(activity)
            }

            override fun onActivityPaused(activity: Activity) {
                androidContextProvider.clearContext()
            }

            // Cleared in onPaused but being extra cautious
            override fun onActivityStopped(activity: Activity) {
                androidContextProvider.clearContext()
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

            // Cleared in onPaused but being extra cautious
            override fun onActivityDestroyed(activity: Activity) {
                androidContextProvider.clearContext()
            }
        })
    }

    private fun initializeKoin() {
        startKoin {
            androidContext(this@DiaryApp)

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
