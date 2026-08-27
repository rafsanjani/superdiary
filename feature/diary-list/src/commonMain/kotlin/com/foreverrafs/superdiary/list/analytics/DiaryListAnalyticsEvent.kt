package com.foreverrafs.superdiary.list.analytics

import com.foreverrafs.superdiary.core.analytics.AnalyticsEvent

internal sealed interface DiaryListAnalyticsEvent : AnalyticsEvent {
    data class DiaryDeleted(
        val count: Int,
    ) : DiaryListAnalyticsEvent {
        override val name: String = "diary_deleted"
        override val parameters: Map<String, String> = mapOf("count" to count.toString())
    }

    data class FavoriteChanged(
        val isFavorite: Boolean,
    ) : DiaryListAnalyticsEvent {
        override val name: String = "favorite_changed"
        override val parameters: Map<String, String> = mapOf(
            "is_favorite" to isFavorite.toString(),
        )
    }
}
