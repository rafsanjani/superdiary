package com.foreverrafs.superdiary.ui.feature.favorites.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.foreverrafs.superdiary.diary.usecase.GetFavoriteDiariesUseCase
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

object FavoriteScreen : Tab {
    @Composable
    override fun Content() {
        val screenModel: FavoritesTabScreenModel = getScreenModel()

        val screenState by screenModel.state.collectAsState()

        FavoriteScreenContent(
            state = screenState,
        )
    }

    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 3u,
            title = "Favorites",
            icon = rememberVectorPainter(Icons.Default.FavoriteBorder),
        )

    override val key: ScreenKey = uniqueScreenKey
}

class FavoritesTabScreenModel(
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
