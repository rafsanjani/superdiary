package com.foreverrafs.superdiary.list.navigation

import kotlinx.serialization.Serializable

sealed interface DiaryListRoute {
    @Serializable
    data object DiaryListScreen : DiaryListRoute
}
