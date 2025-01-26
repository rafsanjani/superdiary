package com.foreverrafs.superdiary.ui.feature.dashboard.screen

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import com.foreverrafs.superdiary.ui.feature.dashboard.DashboardViewModel
import com.foreverrafs.superdiary.ui.navigation.AppRoute
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DashboardTab(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
) {
    val screenModel: DashboardViewModel = koinViewModel()
    val screenState by screenModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    DashboardScreenContent(
        state = screenState,
        onAddEntry = {
            navController.navigate(AppRoute.CreateDiaryScreen)
        },
        onSeeAll = {
            navController.navigate(AppRoute.DiaryListScreen)
        },
        onToggleFavorite = {
            coroutineScope.launch {
                if (screenModel.toggleFavorite(it)) {
                    snackbarHostState.showSnackbar("Favorite Updated")
                }
            }
        },
        onDiaryClick = { diary ->
            diary.id?.let {
                navController.navigate(
                    AppRoute.DetailScreen(it.toString()),
                )
            }
        },
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
    )
}
