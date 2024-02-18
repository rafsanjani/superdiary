package com.foreverrafs.superdiary.core.analytics

fun interface AnalyticsTracker {
    fun trackEvent(event: AnalyticsEvents)
}
