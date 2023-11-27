package com.foreverrafs.superdiary.ui // ktlint-disable filename

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.foreverrafs.superdiary.diary.Database
import com.foreverrafs.superdiary.diary.analytics.Analytics
import com.foreverrafs.superdiary.diary.analytics.AnalyticsEvents
import com.foreverrafs.superdiary.ui.di.appModule
import com.foreverrafs.superdiary.ui.style.SuperdiaryAppTheme
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

private class JvmAnalytics : Analytics {
    override fun trackEvent(event: AnalyticsEvents) {
        TODO("Not yet implemented")
    }
}

fun main() = singleWindowApplication(
    state = WindowState(size = DpSize(400.dp, 800.dp)),
) {
    startKoin {
        modules(appModule(JvmAnalytics()))
    }

    initDatabase()

    SuperdiaryAppTheme {
        App()
    }
}

fun initDatabase() = object : KoinComponent {
    private val database: Database by inject()

    init {
        database.createDatabase()
    }
}
