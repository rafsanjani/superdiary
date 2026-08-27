package com.foreverrafs.superdiary

import com.foreverrafs.superdiary.core.analytics.AnalyticsEvent
import com.foreverrafs.superdiary.core.analytics.AnalyticsTracker

class JvmAnalytics : AnalyticsTracker {
    override fun trackEvent(event: AnalyticsEvent) {
        println("Analytics event: ${event.name} ${event.parameters}")
    }
}
