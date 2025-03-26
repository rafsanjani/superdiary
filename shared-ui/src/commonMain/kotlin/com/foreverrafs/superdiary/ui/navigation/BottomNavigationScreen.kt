package com.foreverrafs.superdiary.ui.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.foreverrafs.auth.model.UserInfo
import com.foreverrafs.superdiary.design.components.SuperDiaryAppBar
import com.foreverrafs.superdiary.ui.components.SuperDiaryBottomBar
import com.foreverrafs.superdiary.ui.feature.dashboard.screen.DashboardTab
import com.foreverrafs.superdiary.ui.feature.diarychat.screen.DiaryChatTab
import com.foreverrafs.superdiary.ui.feature.favorites.screen.FavoriteTab

/**
 * Provides a navigation entry point for all the screens that rely on
 * bottom tab for navigation
 */

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BottomNavigationScreen(
    rootNavController: NavHostController,
    userInfo: UserInfo?,
    onProfileClick: () -> Unit,
    animatedContentScope: AnimatedContentScope,
    sharedTransitionScope: SharedTransitionScope,
    modifier: Modifier = Modifier,
) {
    // This nav controller is used to navigate between the tabs
    val tabNavController = rememberNavController()

    // This snackbar host state is used to show snackbars on the main screen
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier,
        topBar = {
            SuperDiaryAppBar(
                avatarUrl = userInfo?.avatarUrl,
                onProfileClick = onProfileClick,
                animatedContentScope = animatedContentScope,
                sharedTransitionScope = sharedTransitionScope,
            )
        },
        bottomBar = {
            SuperDiaryBottomBar(tabNavController)
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
                    navController = tabNavController,
                    startDestination = BottomNavigationRoute.DashboardTab,
                ) {
                    animatedComposable<BottomNavigationRoute.DashboardTab> {
                        DashboardTab(
                            navController = rootNavController,
                            snackbarHostState = snackbarHostState,
                        )
                    }

                    animatedComposable<BottomNavigationRoute.FavoriteTab> {
                        FavoriteTab(
                            snackbarHostState = snackbarHostState,
                            navController = rootNavController,
                        )
                    }

                    animatedComposable<BottomNavigationRoute.DiaryChatTab> {
                        DiaryChatTab()
                    }
                }
            },
        )
    }
}

private inline fun <reified T : Any> NavGraphBuilder.animatedComposable(
    noinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) = composable<T>(
    content = content,
    enterTransition = { enterTransition() },
    exitTransition = { exitTransition() },
    popEnterTransition = { enterTransition() },
    popExitTransition = { exitTransition() },
)

private fun enterTransition() = fadeIn(
    animationSpec = tween(
        durationMillis = 300,
        easing = LinearEasing,
    ),
)

private fun exitTransition() = fadeOut(
    animationSpec = tween(
        durationMillis = 300,
        easing = LinearEasing,
    ),
)
