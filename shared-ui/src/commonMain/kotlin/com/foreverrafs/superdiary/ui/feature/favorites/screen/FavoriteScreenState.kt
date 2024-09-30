package com.foreverrafs.superdiary.ui.feature.favorites.screen

import com.foreverrafs.superdiary.data.model.Diary

/**
 * We only have two states here because this operation happens on disk.
 * It might sound very hopeful, be we never expect an IOException of any
 * sort to occur so it's either we are loading or we have 0 or n favorites
 */
sealed interface FavoriteScreenState {
    data object Loading : FavoriteScreenState
    data class Content(val diaries: List<Diary>) : FavoriteScreenState
}
