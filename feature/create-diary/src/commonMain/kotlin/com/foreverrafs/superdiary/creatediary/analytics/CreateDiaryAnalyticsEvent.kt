package com.foreverrafs.superdiary.creatediary.analytics

import com.foreverrafs.superdiary.core.analytics.AnalyticsEvent

internal sealed interface CreateDiaryAnalyticsEvent : AnalyticsEvent {
    data class DiaryCreated(
        val hasLocation: Boolean,
    ) : CreateDiaryAnalyticsEvent {
        override val name: String = "diary_created"
        override val parameters: Map<String, String> = mapOf(
            "has_location" to hasLocation.toString(),
        )
    }

    data class AIDiaryRequested(
        val wordCount: Int,
    ) : CreateDiaryAnalyticsEvent {
        override val name: String = "ai_diary_requested"
        override val parameters: Map<String, String> = mapOf(
            "word_count" to wordCount.toString(),
        )
    }
}
