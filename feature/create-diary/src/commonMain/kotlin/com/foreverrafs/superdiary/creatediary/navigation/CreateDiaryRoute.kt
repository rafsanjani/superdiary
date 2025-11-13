package com.foreverrafs.superdiary.creatediary.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface CreateDiaryRoute : NavKey {
    @Serializable
    data object CreateDiary : CreateDiaryRoute
}
