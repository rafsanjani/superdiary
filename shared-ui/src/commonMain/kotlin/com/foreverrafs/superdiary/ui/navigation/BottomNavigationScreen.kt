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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.foreverrafs.auth.model.UserInfo
import com.foreverrafs.superdiary.dashboard.screen.DashboardTab
import com.foreverrafs.superdiary.design.components.AppBar
import com.foreverrafs.superdiary.favorite.screen.FavoriteTab
import com.foreverrafs.superdiary.ui.components.SuperDiaryBottomBar
import com.foreverrafs.superdiary.ui.feature.diarychat.screen.DiaryChatTab

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
    onSeeAll: () -> Unit,
    onDiaryClick: (diaryId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    // This nav controller is used to navigate between the tabs
    val navController = rememberNavController()

    // This snackbar host state is used to show snackbars on the main screen
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier,
        topBar = {
            AppBar(
                avatarUrl = userInfo?.avatarUrl,
                onProfileClick = onProfileClick,
            )
        },
        bottomBar = {
            SuperDiaryBottomBar(navController)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { contentPadding ->

        Surface(
            modifier = Modifier
                .padding(contentPadding)
                .consumeWindowInsets(contentPadding),
            color = MaterialTheme.colorScheme.background,
            content = {
                NavHost(
                    navController = navController,
                    startDestination = BottomNavigationRoute.DashboardTab,
                ) {
                    composable<BottomNavigationRoute.DashboardTab> {
                        DashboardTab(
                            snackbarHostState = snackbarHostState,
                            onAddEntry = onAddEntry,
                            onSeeAll = onSeeAll,
                            onDiaryClick = onDiaryClick,
                        )
                    }

                    composable<BottomNavigationRoute.FavoriteTab> {
                        FavoriteTab(
                            snackbarHostState = snackbarHostState,
                            onFavoriteClick = onDiaryClick,
                        )
                    }

                    composable<BottomNavigationRoute.DiaryChatTab> {
                        DiaryChatTab()
                    }
                }
            },
        )
    }
}
