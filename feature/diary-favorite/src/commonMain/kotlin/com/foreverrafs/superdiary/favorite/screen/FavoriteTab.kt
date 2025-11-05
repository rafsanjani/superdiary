package com.foreverrafs.superdiary.favorite.screen

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.foreverrafs.superdiary.favorite.FavoriteViewModel
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
