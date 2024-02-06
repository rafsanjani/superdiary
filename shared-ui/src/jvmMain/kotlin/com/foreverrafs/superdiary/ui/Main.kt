package com.foreverrafs.superdiary.ui // ktlint-disable filename

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.foreverrafs.superdiary.data.Database
import com.foreverrafs.superdiary.data.analytics.Analytics
import com.foreverrafs.superdiary.data.analytics.AnalyticsEvents
import com.foreverrafs.superdiary.ui.di.appModule
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

private class JvmAnalytics : Analytics {
    override fun trackEvent(event: AnalyticsEvents) {
        TODO("Implement JVM analytics")
    }
}

fun main() = singleWindowApplication(
    state = WindowState(size = DpSize(400.dp, 800.dp)),
) {
    startKoin {
        modules(appModule(JvmAnalytics()))
    }

    initDatabase()

    App()
}

fun initDatabase() = object : KoinComponent {
    private val database: Database by inject()

    init {
        database.createDatabase()
    }
}
