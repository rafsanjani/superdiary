package com.foreverrafs.superdiary.profile.analytics

import com.foreverrafs.superdiary.core.analytics.AnalyticsEvent

internal data object Logout : AnalyticsEvent {
    override val name: String = "logout"
    override val parameters: Map<String, String> = emptyMap()
}
