package com.foreverrafs.superdiary.ui.feature.favorites.screen

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.foreverrafs.superdiary.ui.feature.favorites.FavoriteViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FavoriteTab(
    snackbarHostState: SnackbarHostState,
    onFavoriteClick: (Long) -> Unit,
) {
    val screenModel: FavoriteViewModel = koinViewModel()

    val screenState by screenModel.state.collectAsState()

    screenState?.let {
        FavoriteScreenContent(
            state = it,
            onToggleFavorite = screenModel::toggleFavorite,
            onFavoriteClick = onFavoriteClick,
            snackbarHostState = snackbarHostState,
        )
    }
}
