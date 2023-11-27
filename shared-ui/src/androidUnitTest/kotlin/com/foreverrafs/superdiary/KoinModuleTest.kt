package com.foreverrafs.superdiary

import com.foreverrafs.superdiary.analytics.AndroidAnalytics
import com.foreverrafs.superdiary.diary.di.platformModule
import com.foreverrafs.superdiary.diary.di.useCaseModule
import io.mockk.mockk
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.koinApplication
import org.koin.test.check.checkModules
import kotlin.test.Test

class KoinModuleTest {
    @Test
    fun checkKoinModules() {
        koinApplication {
            androidContext(mockk())
            modules(useCaseModule(), platformModule(AndroidAnalytics()))
            checkModules()
        }
    }
}
