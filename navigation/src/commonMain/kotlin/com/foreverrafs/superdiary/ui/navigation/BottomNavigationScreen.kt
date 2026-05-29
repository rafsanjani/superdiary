package com.foreverrafs.superdiary.ui.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.foreverrafs.auth.model.UserInfo
import com.foreverrafs.superdiary.chat.presentation.screen.DiaryChatTab
import com.foreverrafs.superdiary.dashboard.screen.DashboardTab
import com.foreverrafs.superdiary.favorite.screen.FavoriteTab
import com.foreverrafs.superdiary.list.presentation.list.DiaryListTab
import com.foreverrafs.superdiary.ui.components.SuperDiaryBottomBar

/**
 * Provides a navigation entry point for all the screens that rely on
 * bottom tab for navigation
 */

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BottomNavigationScreen(
    userInfo: UserInfo?,
    onProfileClick: () -> Unit,
    onAddEntry: () -> Unit,
    onDiaryClick: (diaryId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    // This snackbar host state is used to show snackbars on the main screen
    val snackbarHostState = remember { SnackbarHostState() }

    val navigationState = rememberNavigationState(
        startRoute = TopLevelRoute.DashboardTab,
        topLevelRoutes = TopLevelRoute.Items,
        configuration = TopLevelRoute.SavedStateConfiguration,
    )

    val navigator = remember { Navigator(navigationState) }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            SuperDiaryBottomBar(
                items = TopLevelRoute.Items.toList(),
                selected = navigationState.topLevelRoute,
                onItemClick = {
                    navigator.navigate(it)
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { contentPadding ->
        Surface(
            modifier = Modifier
                .padding(paddingValues = contentPadding)
                .consumeWindowInsets(paddingValues = contentPadding),
            color = MaterialTheme.colorScheme.background,
        ) {
            val entryProvider: (NavKey) -> NavEntry<NavKey> = entryProvider {
                entry<TopLevelRoute.DiaryChatTab> {
                    DiaryChatTab(
                        snackbarHostState = snackbarHostState,
                        avatarUrl = userInfo?.avatarUrl,
                        onProfileClick = onProfileClick,
                    )
                }

                entry<TopLevelRoute.DashboardTab> {
                    DashboardTab(
                        snackbarHostState = snackbarHostState,
                        onAddEntry = onAddEntry,
                        onDiaryClick = onDiaryClick,
                        avatarUrl = userInfo?.avatarUrl,
                        onProfileClick = onProfileClick,
                    )
                }

                entry<TopLevelRoute.FavoriteTab> {
                    FavoriteTab(
                        snackbarHostState = snackbarHostState,
                        onFavoriteClick = onDiaryClick,
                        avatarUrl = userInfo?.avatarUrl,
                        onProfileClick = onProfileClick,
                    )
                }

                entry<TopLevelRoute.DiaryList> {
                    DiaryListTab(
                        onAddEntry = onAddEntry,
                        onDiaryClick = onDiaryClick,
                        avatarUrl = userInfo?.avatarUrl,
                        onBackPress = navigator::goBack,
                        onProfileClick = onProfileClick,
                    )
                }
            }

            NavDisplay(
                entries = navigationState.toEntries(entryProvider),
                onBack = navigator::goBack,
            )
        }
    }
}
