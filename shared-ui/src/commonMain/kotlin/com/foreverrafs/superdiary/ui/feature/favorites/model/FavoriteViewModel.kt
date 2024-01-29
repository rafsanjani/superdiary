package com.foreverrafs.superdiary.ui.feature.favorites.model

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.foreverrafs.superdiary.diary.usecase.GetFavoriteDiariesUseCase
import com.foreverrafs.superdiary.ui.feature.favorites.screen.FavoriteScreenState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val getFavoriteDiariesUseCase: GetFavoriteDiariesUseCase,
) :
    StateScreenModel<FavoriteScreenState>(FavoriteScreenState.Loading) {

    fun loadFavorites() = screenModelScope.launch {
        getFavoriteDiariesUseCase().collect { diaries ->
            mutableState.update {
                FavoriteScreenState.Favorites(
                    diaries,
                )
            }
        }
    }
}
