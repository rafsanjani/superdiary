package com.foreverrafs.superdiary.favorite.screen

import androidx.paging.PagingData
import com.foreverrafs.superdiary.domain.model.Diary
import kotlinx.coroutines.flow.Flow

/**
 * We only have two states here because this operation happens on disk.
 * It might sound very hopeful, be we never expect an IOException of any
 * sort to occur so it's either we are loading or we have 0 or n favorites
 */
sealed interface FavoriteScreenState {
    data object Loading : FavoriteScreenState
    data class Content(val diaries: Flow<PagingData<Diary>>) : FavoriteScreenState
}
