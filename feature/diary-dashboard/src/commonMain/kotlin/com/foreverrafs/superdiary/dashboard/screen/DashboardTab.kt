package com.foreverrafs.superdiary.dashboard.screen

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.foreverrafs.superdiary.dashboard.DashboardViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DashboardTab(
    snackbarHostState: SnackbarHostState,
    onAddEntry: () -> Unit,
    onSeeAll: () -> Unit,
    onDiaryClick: (diaryId: Long) -> Unit,
) {
    val screenModel: DashboardViewModel = koinViewModel()
    val screenState by screenModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    DashboardScreenContent(
        state = screenState,
        onAddEntry = onAddEntry,
        onSeeAll = onSeeAll,
        onToggleFavorite = {
            coroutineScope.launch {
                if (screenModel.toggleFavorite(it)) {
                    snackbarHostState.showSnackbar("Favorite Updated")
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
        onEnableBiometric = screenModel::onEnableBiometricAuth,
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
