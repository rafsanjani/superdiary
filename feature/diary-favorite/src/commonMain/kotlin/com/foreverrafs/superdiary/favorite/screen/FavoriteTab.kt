package com.foreverrafs.superdiary.favorite.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalOf
import com.foreverrafs.superdiary.favorite.FavoriteViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FavoriteTab(
    snackbarHostState: SnackbarHostState,
    onFavoriteClick: (Long) -> Unit,
    avatarUrl: String?,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    modifierLocalOf { }
    val screenModel: FavoriteViewModel = koinViewModel()

    val screenState by screenModel.state.collectAsState()

    screenState?.let {
        FavoriteScreenContent(
            state = it,
            onToggleFavorite = screenModel::toggleFavorite,
            onFavoriteClick = onFavoriteClick,
            snackbarHostState = snackbarHostState,
            modifier = modifier.fillMaxSize(),
            avatarUrl = avatarUrl,
            onProfileClick = onProfileClick,
        )
    }
}
