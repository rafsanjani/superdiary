package com.foreverrafs.superdiary.ui.feature.favorites

import com.foreverrafs.superdiary.diary.model.Diary

sealed interface FavoritesTabScreenState {
    data object Idle : FavoritesTabScreenState
    data class Favorites(val diaries: List<Diary>) : FavoritesTabScreenState
}