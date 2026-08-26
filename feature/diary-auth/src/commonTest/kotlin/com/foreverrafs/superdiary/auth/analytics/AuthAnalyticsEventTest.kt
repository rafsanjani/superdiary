package com.foreverrafs.superdiary.auth.analytics

import kotlin.test.Test
import kotlin.test.assertEquals

class AuthAnalyticsEventTest {
    @Test
    fun `login includes authentication method`() {
        val event = AuthAnalyticsEvent.Login(method = "google")

        assertEquals("login", event.name)
        assertEquals(mapOf("method" to "google"), event.parameters)
    }

    @Test
    fun `registration includes authentication method`() {
        val event = AuthAnalyticsEvent.Registration(method = "email")

        assertEquals("sign_up", event.name)
        assertEquals(mapOf("method" to "email"), event.parameters)
    }
}
