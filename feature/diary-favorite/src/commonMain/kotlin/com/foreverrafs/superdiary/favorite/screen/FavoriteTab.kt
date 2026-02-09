package com.foreverrafs.superdiary.favorite.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.foreverrafs.superdiary.favorite.FavoriteViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FavoriteTab(
    snackbarHostState: SnackbarHostState,
    onFavoriteClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val screenModel: FavoriteViewModel = koinViewModel()

    val screenState by screenModel.state.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
    ) {
        screenState?.let {
            FavoriteScreenContent(
                state = it,
                onToggleFavorite = screenModel::toggleFavorite,
                onFavoriteClick = onFavoriteClick,
                snackbarHostState = snackbarHostState,
            )
        }
    }
}
