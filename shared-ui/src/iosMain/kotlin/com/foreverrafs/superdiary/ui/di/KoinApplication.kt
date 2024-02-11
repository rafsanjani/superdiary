package com.foreverrafs.superdiary.ui.di

import com.foreverrafs.superdiary.data.analytics.Analytics
import org.koin.core.context.startKoin

@Suppress("unused")
object KoinApplication {
    fun initialize(
        analytics: Analytics,
    ) = startKoin {
        modules(compositeModule(analytics))
    }
}
