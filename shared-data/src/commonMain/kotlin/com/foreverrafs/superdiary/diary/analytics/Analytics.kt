package com.foreverrafs.superdiary.diary.analytics

fun interface Analytics {
    fun trackEvent(event: AnalyticsEvents)
}
