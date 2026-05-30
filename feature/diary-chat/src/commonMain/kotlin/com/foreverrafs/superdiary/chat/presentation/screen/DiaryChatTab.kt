package com.foreverrafs.superdiary.chat.presentation.screen

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.foreverrafs.superdiary.chat.presentation.DiaryChatViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DiaryChatTab(
    snackbarHostState: SnackbarHostState,
    avatarUrl: String?,
    onProfileClick: () -> Unit = {},
) {
    val viewModel: DiaryChatViewModel = koinViewModel()
    val screenState by viewModel.viewState.collectAsState()

    DiaryChatScreenContent(
        snackbarHostState = snackbarHostState,
        avatarUrl = avatarUrl,
        screenState = screenState,
        onQueryDiaries = viewModel::queryDiaries,
        onDismissError = viewModel::dismissError,
        onProfileClick = onProfileClick,
    )
}
