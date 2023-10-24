package com.foreverrafs.superdiary.ui.feature.favorites

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.foreverrafs.superdiary.diary.usecase.GetAllDiariesUseCase
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

object FavoritesTab : Tab {
    @Composable
    override fun Content() {
        val screenModel: FavoritesTabScreenModel = getScreenModel()

        val screenState by screenModel.state.collectAsState()

        FavoritesTabScreen(
            state = screenState,
        )
    }

    override val key: ScreenKey = uniqueScreenKey

    override val options: TabOptions
        @Composable get() {
            val title = "Home"
            val icon = rememberVectorPainter(Icons.Default.Favorite)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon,
                )
            }
        }
}

class FavoritesTabScreenModel(
    private val getAllDiariesUseCase: GetAllDiariesUseCase,
) :
    StateScreenModel<FavoritesTabScreenState>(FavoritesTabScreenState.Idle) {

    init {
        loadFavorites()
    }

    private fun loadFavorites() = screenModelScope.launch {
        getAllDiariesUseCase().collect { diaries ->
            mutableState.update {
                FavoritesTabScreenState.Favorites(
                    diaries,
                )
            }
        }
    }
}
