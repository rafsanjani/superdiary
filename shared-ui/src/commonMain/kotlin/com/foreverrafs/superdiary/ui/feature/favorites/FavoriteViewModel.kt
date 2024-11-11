package com.foreverrafs.superdiary.ui.feature.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.data.usecase.GetFavoriteDiariesUseCase
import com.foreverrafs.superdiary.data.usecase.UpdateDiaryUseCase
import com.foreverrafs.superdiary.ui.feature.favorites.screen.FavoriteScreenState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val getFavoriteDiariesUseCase: GetFavoriteDiariesUseCase,
    private val updateDiaryUseCase: UpdateDiaryUseCase,
    private val logger: AggregateLogger,
) : ViewModel() {

    private val mutableState = MutableStateFlow<FavoriteScreenState?>(FavoriteScreenState.Loading)

    private val favoriteDiaries: Flow<List<Diary>> = getFavoriteDiariesUseCase()

    val state: StateFlow<FavoriteScreenState?> = mutableState
        .onStart { loadFavorites() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FavoriteScreenState.Loading,
        )

    private fun loadFavorites() = viewModelScope.launch {
        logger.i(Tag) {
            "Loading favorites"
        }

        favoriteDiaries.collect { diaries ->
            mutableState.update {
                logger.i(Tag) {
                    "Loaded ${diaries.size} favorite entries"
                }
                FavoriteScreenState.Content(
                    diaries,
                )
            }
        }
    }

    suspend fun toggleFavorite(diary: Diary): Boolean {
        logger.i(Tag) {
            "Toggling favorite from favorite=${diary.isFavorite} to favorite=${!diary.isFavorite}"
        }

        val result = updateDiaryUseCase(
            diary.copy(
                isFavorite = !diary.isFavorite,
            ),
        )

        return when (result) {
            is Result.Failure -> {
                logger.e(Tag, result.error) {
                    "Error toggling favorite from ${diary.isFavorite} to ${!diary.isFavorite}"
                }
                false
            }

            is Result.Success -> {
                logger.d(Tag) {
                    "Favorite toggled from ${!diary.isFavorite} to ${diary.isFavorite}"
                }
                result.data
            }
        }
    }

    companion object {
        private val Tag = FavoriteViewModel::class.simpleName.orEmpty()
    }
}
