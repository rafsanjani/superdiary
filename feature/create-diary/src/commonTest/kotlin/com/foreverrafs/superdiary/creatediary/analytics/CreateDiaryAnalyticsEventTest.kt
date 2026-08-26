package com.foreverrafs.superdiary.creatediary.analytics

import kotlin.test.Test
import kotlin.test.assertEquals

class CreateDiaryAnalyticsEventTest {
    @Test
    fun `diary created includes location state`() {
        val event = CreateDiaryAnalyticsEvent.DiaryCreated(hasLocation = true)

        assertEquals("diary_created", event.name)
        assertEquals(mapOf("has_location" to "true"), event.parameters)
    }

    @Test
    fun `AI diary requested includes word count`() {
        val event = CreateDiaryAnalyticsEvent.AIDiaryRequested(wordCount = 250)

        assertEquals("ai_diary_requested", event.name)
        assertEquals(mapOf("word_count" to "250"), event.parameters)
    }
}
