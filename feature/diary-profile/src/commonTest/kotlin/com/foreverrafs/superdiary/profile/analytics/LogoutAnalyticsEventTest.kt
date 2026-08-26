package com.foreverrafs.superdiary.profile.analytics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LogoutAnalyticsEventTest {
    @Test
    fun `logout has no parameters`() {
        assertEquals("logout", Logout.name)
        assertTrue(Logout.parameters.isEmpty())
    }
}
