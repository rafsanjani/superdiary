package com.foreverrafs.superdiary.list.analytics

import kotlin.test.Test
import kotlin.test.assertEquals

class DiaryListAnalyticsEventTest {
    @Test
    fun `diary deleted includes deleted count`() {
        val event = DiaryListAnalyticsEvent.DiaryDeleted(count = 3)

        assertEquals("diary_deleted", event.name)
        assertEquals(mapOf("count" to "3"), event.parameters)
    }

    @Test
    fun `favorite changed includes current favorite state`() {
        val event = DiaryListAnalyticsEvent.FavoriteChanged(isFavorite = true)

        assertEquals("favorite_changed", event.name)
        assertEquals(mapOf("is_favorite" to "true"), event.parameters)
    }
}
