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
import com.foreverrafs.superdiary.ui.SuperDiaryScreen
import com.foreverrafs.superdiary.ui.feature.favorites.model.FavoriteScreenModel

object FavoriteScreen : SuperDiaryScreen {
    @Composable
    override fun Content() {
        val screenModel: FavoriteScreenModel = getScreenModel()

        val screenState by screenModel.state.collectAsState()

        LaunchedEffect(Unit){
            screenModel.loadFavorites()
        }

        FavoriteScreenContent(
            state = screenState,
        )
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
