package com.foreverrafs.superdiary

import com.foreverrafs.superdiary.core.analytics.AnalyticsEvent
import com.foreverrafs.superdiary.core.analytics.AnalyticsTracker
import io.sentry.Breadcrumb
import io.sentry.Sentry
import io.sentry.SentryLevel

class AndroidAnalytics : AnalyticsTracker {
    override fun trackEvent(event: AnalyticsEvent) {
        val breadcrumb = Breadcrumb().apply {
            category = "analytics"
            message = event.name
            level = SentryLevel.INFO
            type = "user"
            event.parameters.forEach(::setData)
        }

        Sentry.addBreadcrumb(breadcrumb)
    }
}
