package com.foreverrafs.superdiary.ui.feature.favorites.screen

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.foreverrafs.superdiary.ui.feature.favorites.FavoriteViewModel
import com.foreverrafs.superdiary.ui.navigation.AppRoute
import org.koin.compose.koinInject

@Composable
fun FavoriteTab(
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
                navController.navigate(AppRoute.DetailScreen(favorite.toString()))
            },
            snackbarHostState = snackbarHostState,
        )
    }
}
