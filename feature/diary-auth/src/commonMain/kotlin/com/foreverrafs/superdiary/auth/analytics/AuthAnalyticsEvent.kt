package com.foreverrafs.superdiary.auth.analytics

import com.foreverrafs.superdiary.core.analytics.AnalyticsEvent

internal sealed interface AuthAnalyticsEvent : AnalyticsEvent {
    data class Login(
        val method: String,
    ) : AuthAnalyticsEvent {
        override val name: String = "login"
        override val parameters: Map<String, String> = mapOf("method" to method)
    }

    data class Registration(
        val method: String,
    ) : AuthAnalyticsEvent {
        override val name: String = "sign_up"
        override val parameters: Map<String, String> = mapOf("method" to method)
    }
}
