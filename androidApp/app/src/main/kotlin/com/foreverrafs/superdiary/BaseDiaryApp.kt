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
import org.koin.core.context.startKoin
import org.koin.core.module.Module

abstract class BaseDiaryApp : Application(), KoinComponent {
    private val androidContextProvider = AndroidContextProvider.getInstance()
    override fun onCreate() {
        super.onCreate()
        initializeKoin()
        registerAndroidContextProviderCallbacks()
    }

    private fun registerAndroidContextProviderCallbacks() {
        registerActivityLifecycleCallbacks(
            object : ActivityLifecycleCallbacks {
                override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) =
                    Unit

                override fun onActivityStarted(activity: Activity) = Unit

                // Sometimes activities will cycle between paused and resumed states
                override fun onActivityResumed(activity: Activity) {
                    androidContextProvider.setContext(activity)
                }

                override fun onActivityPaused(activity: Activity) {
                    androidContextProvider.clearContext()
                }

                override fun onActivityStopped(activity: Activity) = Unit

                override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) =
                    Unit

                override fun onActivityDestroyed(activity: Activity) = Unit
            },
        )
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
