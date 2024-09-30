package com.foreverrafs.superdiary.ui.feature.favorites.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.navigation.NavController
import com.foreverrafs.superdiary.ui.feature.details.DetailScreen
import com.foreverrafs.superdiary.ui.feature.favorites.model.FavoriteViewModel
import com.foreverrafs.superdiary.ui.navigation.SuperDiaryTab
import com.foreverrafs.superdiary.ui.navigation.TabOptions
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@Serializable
object FavoriteTab : SuperDiaryTab {
    @Composable
    fun Content(
        snackbarHostState: SnackbarHostState,
        navController: NavController,
    ) {
        val screenModel: FavoriteViewModel = koinInject()

        val screenState by screenModel.state.collectAsState()

        screenState?.let {
            FavoriteScreenContent(
                it,
                onToggleFavorite = screenModel::toggleFavorite,
                onFavoriteClick = { favorite ->
                    navController.navigate(DetailScreen(favorite.toString()))
                },
                snackbarHostState = snackbarHostState,
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
