package com.foreverrafs.superdiary.data.analytics

fun interface Analytics {
    fun trackEvent(event: AnalyticsEvents)
}
