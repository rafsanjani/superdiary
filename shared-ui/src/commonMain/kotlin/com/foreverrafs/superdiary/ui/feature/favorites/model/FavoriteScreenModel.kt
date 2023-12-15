package com.foreverrafs.superdiary.ui.feature.favorites.model

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.foreverrafs.superdiary.diary.usecase.GetFavoriteDiariesUseCase
import com.foreverrafs.superdiary.ui.feature.favorites.screen.FavoriteScreenState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoriteScreenModel(
    private val getAllDiariesUseCase: GetFavoriteDiariesUseCase,
) :
    StateScreenModel<FavoriteScreenState>(FavoriteScreenState.Idle) {

    init {
        loadFavorites()
    }

    private fun loadFavorites() = screenModelScope.launch {
        getAllDiariesUseCase().collect { diaries ->
            mutableState.update {
                FavoriteScreenState.Favorites(
                    diaries,
                )
            }
        }
    }
}
