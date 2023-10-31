package com.foreverrafs.superdiary.diary.usecase

import com.foreverrafs.superdiary.diary.di.platformModule
import com.foreverrafs.superdiary.diary.di.useCaseModule
import org.koin.dsl.koinApplication
import org.koin.test.check.checkModules
import kotlin.test.Test

class KoinModuleTest {
    @Test
    fun checkKoinModules() {
        koinApplication {
            modules(useCaseModule(), platformModule())
            checkModules()
        }
    }
}
