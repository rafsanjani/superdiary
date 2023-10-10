package com.foreverrafs.superdiary.ui // ktlint-disable filename

import androidx.compose.ui.window.singleWindowApplication
import com.foreverrafs.superdiary.ui.inject.appModule
import com.foreverrafs.superdiary.ui.style.AppTheme
import org.koin.core.context.startKoin

fun main() = singleWindowApplication {
    startKoin {
        modules(appModule())
    }

    AppTheme {
        App()
    }
}
