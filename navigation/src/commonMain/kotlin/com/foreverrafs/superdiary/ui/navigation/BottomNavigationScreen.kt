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
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.foreverrafs.auth.model.UserInfo
import com.foreverrafs.superdiary.chat.presentation.screen.DiaryChatTab
import com.foreverrafs.superdiary.dashboard.screen.DashboardTab
import com.foreverrafs.superdiary.design.components.AppBar
import com.foreverrafs.superdiary.favorite.screen.FavoriteTab
import com.foreverrafs.superdiary.ui.components.SuperDiaryBottomBar
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

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
    // This snackbar host state is used to show snackbars on the main screen
    val snackbarHostState = remember { SnackbarHostState() }

    val backstack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(TopLevelRoute.DashboardTab::class, TopLevelRoute.DashboardTab.serializer())
                    subclass(TopLevelRoute.FavoriteTab::class, TopLevelRoute.FavoriteTab.serializer())
                    subclass(TopLevelRoute.DiaryChatTab::class, TopLevelRoute.DiaryChatTab.serializer())
                }
            }
        },
        TopLevelRoute.DashboardTab,
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            AppBar(
                avatarUrl = userInfo?.avatarUrl,
                onProfileClick = onProfileClick,
            )
        },
        bottomBar = {
            SuperDiaryBottomBar(
                tabs = TopLevelRoute.Items,
                onTabSelected = { tab ->
//                    navController.navigate(tab) {
//                        popUpTo(navController.graph.startDestinationId) {
//                            saveState = true
//                        }
//                        launchSingleTop = true
//                        restoreState = true
//                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { contentPadding ->
        Surface(
            modifier = Modifier
                .padding(contentPadding)
                .consumeWindowInsets(contentPadding),
            color = MaterialTheme.colorScheme.background,
        ) {
            NavDisplay(
                backStack = backstack,
                modifier = Modifier
                    .padding(paddingValues = contentPadding)
                    .consumeWindowInsets(paddingValues = contentPadding),
                entryProvider = entryProvider {
                    entry<TopLevelRoute.DiaryChatTab> {
                        DiaryChatTab()
                    }

                    entry<TopLevelRoute.DashboardTab> {
                        val backstack = TopLevelRoute.DashboardTab.backStack

                        DashboardTab(
                            snackbarHostState = snackbarHostState,
                            onAddEntry = onAddEntry,
                            onSeeAll = onSeeAll,
                            onDiaryClick = onDiaryClick,
                        )
                    }

                    entry<TopLevelRoute.FavoriteTab> {
                        FavoriteTab(
                            snackbarHostState = snackbarHostState,
                            onFavoriteClick = onDiaryClick,
                        )
                    }
                },
            )
        }
    }
}
