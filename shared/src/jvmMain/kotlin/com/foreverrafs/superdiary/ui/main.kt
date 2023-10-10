package com.foreverrafs.superdiary.ui

import androidx.compose.ui.window.singleWindowApplication
import com.foreverrafs.superdiary.ui.inject.appModule
import org.koin.core.context.startKoin

// ktlint-disable filename

fun main() = singleWindowApplication {
    startKoin {
        modules(appModule())
    }

    AppTheme {
        App()
    }
}
