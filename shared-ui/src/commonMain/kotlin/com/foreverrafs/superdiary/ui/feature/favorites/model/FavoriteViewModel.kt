package com.foreverrafs.superdiary.ui.feature.favorites.model

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.data.usecase.GetFavoriteDiariesUseCase
import com.foreverrafs.superdiary.data.usecase.UpdateDiaryUseCase
import com.foreverrafs.superdiary.ui.feature.favorites.screen.FavoriteScreenState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val getFavoriteDiariesUseCase: GetFavoriteDiariesUseCase,
    private val updateDiaryUseCase: UpdateDiaryUseCase,
) :
    StateScreenModel<FavoriteScreenState?>(null) {

    fun loadFavorites() = screenModelScope.launch {
        getFavoriteDiariesUseCase().collect { diaries ->
            mutableState.update {
                FavoriteScreenState.Content(
                    diaries,
                )
            }
        }
    }

    suspend fun toggleFavorite(diary: Diary): Boolean = updateDiaryUseCase(
        diary.copy(isFavorite = !diary.isFavorite),
    )
}
