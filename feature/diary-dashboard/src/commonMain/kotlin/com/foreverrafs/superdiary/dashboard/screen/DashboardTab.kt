package com.foreverrafs.superdiary.dashboard.screen

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.foreverrafs.auth.rememberAuthUiContext
import com.foreverrafs.superdiary.dashboard.DashboardViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import superdiary.feature.diary_dashboard.generated.resources.Res
import superdiary.feature.diary_dashboard.generated.resources.favorite_updated_message

@Composable
fun DashboardTab(
    snackbarHostState: SnackbarHostState,
    onAddEntry: () -> Unit,
    onDiaryClick: (diaryId: Long) -> Unit,
    avatarUrl: String?,
    onProfileClick: () -> Unit,
) {
    val screenModel: DashboardViewModel = koinViewModel()
    val authUiContext = rememberAuthUiContext()
    val screenState by screenModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val favoriteUpdatedMessage = stringResource(Res.string.favorite_updated_message)

    DashboardScreenContent(
        avatarUrl = avatarUrl,
        onProfileClick = onProfileClick,
        state = screenState,
        onAddEntry = onAddEntry,
        onToggleFavorite = {
            coroutineScope.launch {
                if (screenModel.toggleFavorite(it)) {
                    snackbarHostState.showSnackbar(favoriteUpdatedMessage)
                }
            }
        },
        onDiaryClick = onDiaryClick,
        onDisableBiometricAuth = {
            screenModel.onUpdateSettings {
                it.copy(
                    showBiometricAuthDialog = false,
                )
            }
        },
        onEnableBiometric = { screenModel.onEnableBiometricAuth(authUiContext) },
        onToggleLatestEntries = {
            screenModel.onUpdateSettings {
                it.copy(
                    showLatestEntries = !it.showLatestEntries,
                )
            }
        },
        onToggleGlanceCard = {
            screenModel.onUpdateSettings {
                it.copy(
                    showAtAGlance = !it.showAtAGlance,
                )
            }
        },
        onToggleWeeklySummaryCard = {
            screenModel.onUpdateSettings {
                it.copy(
                    showWeeklySummary = !it.showWeeklySummary,
                )
            }
        },
        snackbarHostState = snackbarHostState,
        onRetry = screenModel::onRetry,
    )
}
