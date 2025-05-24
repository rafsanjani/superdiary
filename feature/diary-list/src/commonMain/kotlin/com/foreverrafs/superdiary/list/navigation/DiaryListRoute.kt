package com.foreverrafs.superdiary.list.navigation

import kotlinx.serialization.Serializable

sealed interface DiaryListRoute {
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
