package com.foreverrafs.superdiary.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigation3.ui.NavDisplay
import com.foreverrafs.auth.model.UserInfo
import com.foreverrafs.superdiary.chat.presentation.screen.DiaryChatTab
import com.foreverrafs.superdiary.dashboard.screen.DashboardTab
import com.foreverrafs.superdiary.design.style.LocalRootAnimatedContentScope
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
    val rootAnimatedContentScope = LocalNavAnimatedContentScope.current

    val snackbarHostState = remember { SnackbarHostState() }

    val navigationState = rememberNavigationState(
        startRoute = TopLevelRoute.DashboardTab,
        topLevelRoutes = TopLevelRoute.Items,
        configuration = TopLevelRoute.SavedStateConfiguration,
    )

    val navigator = remember { Navigator(navigationState) }

    // Tab order determines slide direction.
    val tabOrder = remember { TopLevelRoute.Items.toList() }
    var previousTabIndex by remember { mutableIntStateOf(0) }
    val currentTabIndex = tabOrder.indexOf(navigationState.topLevelRoute)
    val slideDirection = (currentTabIndex - previousTabIndex).coerceIn(-1, 1)

    LaunchedEffect(navigationState.topLevelRoute) {
        previousTabIndex = currentTabIndex
    }

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

            CompositionLocalProvider(
                LocalRootAnimatedContentScope provides rootAnimatedContentScope,
            ) {
                NavDisplay(
                    entries = navigationState.toEntries(entryProvider),
                    onBack = navigator::goBack,
                    transitionSpec = {
                        tabContentTransform(slideDirection)
                    },
                    popTransitionSpec = {
                        tabContentTransform(-slideDirection)
                    },
                )
            }
        }
    }
}

/**
 * Returns a [ContentTransform] that slides the tab content in the given direction.
 *
 * @param direction +1: new content enters from the right, current exits to the left.
 *                    -1: new content enters from the left, current exits to the right.
 */
private fun AnimatedContentTransitionScope<*>.tabContentTransform(
    direction: Int,
) = if (direction >= 0) {
    slideInHorizontally(
        animationSpec = tween(300),
        initialOffsetX = { fullWidth -> fullWidth },
    ) togetherWith slideOutHorizontally(
        animationSpec = tween(300),
        targetOffsetX = { fullWidth -> -fullWidth },
    )
} else {
    slideInHorizontally(
        animationSpec = tween(300),
        initialOffsetX = { fullWidth -> -fullWidth },
    ) togetherWith slideOutHorizontally(
        animationSpec = tween(300),
        targetOffsetX = { fullWidth -> fullWidth },
    )
}
