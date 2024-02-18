package com.foreverrafs.superdiary.ui.feature.favorites.model

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.foreverrafs.superdiary.core.logging.Logger
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.data.usecase.GetFavoriteDiariesUseCase
import com.foreverrafs.superdiary.data.usecase.UpdateDiaryUseCase
import com.foreverrafs.superdiary.ui.feature.favorites.screen.FavoriteScreenState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val getFavoriteDiariesUseCase: GetFavoriteDiariesUseCase,
    private val updateDiaryUseCase: UpdateDiaryUseCase,
    private val logger: Logger,
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

    suspend fun toggleFavorite(diary: Diary): Boolean {
        val result = updateDiaryUseCase(
            diary.copy(
                isFavorite = !diary.isFavorite,
            ),
        )

        return when (result) {
            is Result.Failure -> {
                logger.e(Tag, result.error) {
                    "Error toggling favorite"
                }
                false
            }

            is Result.Success -> {
                logger.d(Tag) {
                    "Favorite toggled"
                }
                result.data
            }
        }
    }

    companion object {
        private val Tag = FavoriteViewModel::class.simpleName.orEmpty()
    }
}
