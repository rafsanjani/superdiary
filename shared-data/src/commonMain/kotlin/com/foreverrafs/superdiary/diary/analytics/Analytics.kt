package com.foreverrafs.superdiary.diary.analytics

interface Analytics {
    fun trackEvent(event: AnalyticsEvents)
}
