package com.foreverrafs.superdiary

import com.foreverrafs.superdiary.core.analytics.AnalyticsTracker
import com.foreverrafs.superdiary.ui.di.compositeModule
import io.mockk.mockk
import kotlin.test.Test
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.koinApplication
import org.koin.test.check.checkModules

class KoinModuleTest {
    private val emptyAnalyticsTracker = AnalyticsTracker {
        // no-op
    }

    @Test
    fun checkKoinModules() {
        koinApplication {
            androidContext(mockk())
            modules(compositeModule(emptyAnalyticsTracker))
            checkModules()
        }
    }
}
