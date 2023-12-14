package com.foreverrafs.superdiary.ui.feature.favorites.screen

import com.foreverrafs.superdiary.diary.model.Diary

sealed interface FavoriteScreenState {
    data object Idle : FavoriteScreenState
    data class Favorites(val diaries: List<Diary>) : FavoriteScreenState
}
