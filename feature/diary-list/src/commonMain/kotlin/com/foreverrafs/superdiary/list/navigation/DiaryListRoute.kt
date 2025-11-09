package com.foreverrafs.superdiary.list.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

internal sealed interface DiaryListRoute : NavKey {
    @Serializable
    data object DiaryListScreen : DiaryListRoute

    @Serializable
    data class DetailScreen(val diaryId: String) : DiaryListRoute {
        companion object {
            const val URI_PATH = "https://api.nebulainnova.co.uk/details"
            const val DEEPLINK_URI_PATTERN = "$URI_PATH/{diaryId}"
        }
    }
}
