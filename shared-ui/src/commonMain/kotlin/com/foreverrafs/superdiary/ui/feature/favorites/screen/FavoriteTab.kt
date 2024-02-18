package com.foreverrafs.superdiary.ui.feature.favorites.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.foreverrafs.superdiary.ui.LocalScreenNavigator
import com.foreverrafs.superdiary.ui.SuperDiaryTab
import com.foreverrafs.superdiary.ui.feature.details.DetailScreen
import com.foreverrafs.superdiary.ui.feature.favorites.model.FavoriteViewModel

object FavoriteTab : SuperDiaryTab {
    @Composable
    override fun Content() {
        val screenModel: FavoriteViewModel = getScreenModel()

        val screenState by screenModel.state.collectAsState()
        val navigator = LocalScreenNavigator.current

        LaunchedEffect(Unit) {
            screenModel.loadFavorites()
        }

        screenState?.let {
            FavoriteScreenContent(
                it,
                onToggleFavorite = screenModel::toggleFavorite,
                onFavoriteClicked = { favorite ->
                    navigator.push(DetailScreen(favorite))
                },
            )
        }
    }

    override val selectedIcon: VectorPainter
        @Composable
        get() = rememberVectorPainter(Icons.Default.Favorite)

    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 3u,
            title = "Favorites",
            icon = rememberVectorPainter(Icons.Default.FavoriteBorder),
        )
}
