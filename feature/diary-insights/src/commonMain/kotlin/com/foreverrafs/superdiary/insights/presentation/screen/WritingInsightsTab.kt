package com.foreverrafs.superdiary.insights.presentation.screen

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.foreverrafs.superdiary.insights.presentation.WritingInsightsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WritingInsightsTab(
    snackbarHostState: SnackbarHostState,
    avatarUrl: String?,
    onProfileClick: () -> Unit = {},
) {
    val viewModel: WritingInsightsViewModel = koinViewModel()
    val screenState by viewModel.viewState.collectAsState()

    WritingInsightsScreenContent(
        screenState = screenState,
        snackbarHostState = snackbarHostState,
        avatarUrl = avatarUrl,
        onProfileClick = onProfileClick,
        onRefresh = viewModel::refresh,
        onDismissError = viewModel::dismissError,
    )
}
