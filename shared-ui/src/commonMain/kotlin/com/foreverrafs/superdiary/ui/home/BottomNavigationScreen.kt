package com.foreverrafs.superdiary.ui.home

import androidx.compose.animation.AnimatedContentScope
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.foreverrafs.superdiary.ui.AppSessionState
import com.foreverrafs.superdiary.ui.AppViewModel
import com.foreverrafs.superdiary.ui.components.ConfirmLogoutDialog
import com.foreverrafs.superdiary.ui.components.SuperDiaryAppBar
import com.foreverrafs.superdiary.ui.components.SuperDiaryBottomBar
import com.foreverrafs.superdiary.ui.feature.auth.login.screen.LoginScreen
import com.foreverrafs.superdiary.ui.feature.dashboard.screen.DashboardTab
import com.foreverrafs.superdiary.ui.feature.diarychat.screen.DiaryChatTab
import com.foreverrafs.superdiary.ui.feature.favorites.screen.FavoriteTab
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

/**
 * Provides a navigation entry point for all the screens that rely on
 * bottom tab for navigation
 */

@Serializable
object BottomNavigationScreen {

    @Composable
    fun Content(
        rootNavController: NavHostController,
        modifier: Modifier = Modifier,
    ) {
        // This nav controller is used to navigate between the tabs
        val tabNavController = rememberNavController()
        val appViewModel: AppViewModel = koinInject()

        val viewState by appViewModel.viewState.collectAsStateWithLifecycle()

        // This snackbar host state is used to show snackbars on the main screen
        val snackbarHostState = remember { SnackbarHostState() }

        var showConfirmLogoutDialog by remember { mutableStateOf(false) }

        if (showConfirmLogoutDialog) {
            ConfirmLogoutDialog(
                onDismiss = {
                    showConfirmLogoutDialog = false
                },
                onLogout = {
                    appViewModel.logOut()
                    rootNavController.navigate(LoginScreen) {
                        popUpTo(LoginScreen) {
                            inclusive = true
                        }
                    }
                    showConfirmLogoutDialog = false
                },
            )
        }

        Scaffold(
            modifier = modifier,
            topBar = {
                SuperDiaryAppBar(
                    userInfo = (viewState as? AppSessionState.Success)?.userInfo,
                    onProfileClick = {
                        showConfirmLogoutDialog = true
                    },
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
                        startDestination = DashboardTab,
                    ) {
                        animatedComposable<DashboardTab> {
                            DashboardTab.Content(
                                navController = rootNavController,
                                snackbarHostState = snackbarHostState,
                            )
                        }

                        animatedComposable<FavoriteTab> {
                            FavoriteTab.Content(
                                snackbarHostState = snackbarHostState,
                                navController = rootNavController,
                            )
                        }

                        animatedComposable<DiaryChatTab> {
                            DiaryChatTab.Content()
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
            300,
            easing = LinearEasing,
        ),
    )

    private fun exitTransition() = fadeOut(
        animationSpec = tween(
            300,
            easing = LinearEasing,
        ),
    )
}
