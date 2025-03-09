@file:Suppress("ktlint:standard:filename")

package com.foreverrafs.superdiary

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.ui.App
import com.foreverrafs.superdiary.ui.di.compositeModule
import org.jetbrains.compose.reload.DevelopmentEntryPoint
import org.koin.core.context.GlobalContext.startKoin

fun main() = singleWindowApplication(
    state = WindowState(size = DpSize(400.dp, 800.dp)),
) {
    initializeKoin()
    DevelopmentEntryPoint {
        App()
    }
}

private fun initializeKoin() {
    startKoin {
        modules(
            compositeModule(
                JvmAnalytics(),
                AggregateLogger(emptyList()),
            ),
        )
    }
}
