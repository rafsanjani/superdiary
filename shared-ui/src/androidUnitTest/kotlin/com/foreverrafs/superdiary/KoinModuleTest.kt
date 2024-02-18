package com.foreverrafs.superdiary

import com.foreverrafs.superdiary.analytics.AndroidAnalytics
import com.foreverrafs.superdiary.ui.di.compositeModule
import io.mockk.mockk
import kotlin.test.Test
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.koinApplication
import org.koin.test.check.checkModules

class KoinModuleTest {
    @Test
    fun checkKoinModules() {
        koinApplication {
            androidContext(mockk())
            modules(compositeModule(AndroidAnalytics()))
            checkModules()
        }
    }
}
