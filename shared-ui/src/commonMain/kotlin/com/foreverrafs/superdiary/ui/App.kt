package com.foreverrafs.superdiary.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.foreverrafs.superdiary.ui.home.BottomNavigationScreen
import com.foreverrafs.superdiary.ui.style.SuperdiaryTheme

/**
 * Entry point into the whole app. In an ideal world we'll only just render
 * this composable on individual platforms but the world isn't ideal, huh!
 */

@Composable
fun App(modifier: Modifier = Modifier) {
    SuperdiaryTheme {
        Navigator(
            screen = BottomNavigationScreen,
            onBackPressed = {
                SuperDiaryBackPressHandler.execute()
            },
        ) { navigator ->
            val snackbarHostState = SnackbarHostState()

            CompositionLocalProvider(
                LocalRootSnackbarHostState provides snackbarHostState,
                LocalScreenNavigator provides navigator,
            ) {
                // Wrap it inside this box just to allow setting
                // testTagAsResourceId from Android land
                Box(modifier = modifier) {
                    SlideTransition(navigator)
                }
            }
        }
    }
}

/**
 * In voyager we are not really able to perform screen navigation from
 * within a tab navigator. LocalNavigator.push causes an exception because
 * it expects a Tab when used from within a TabNavigator. This way we
 * are able to implicitly pass the root navigator around to be used for
 * navigation by individual tabs
 */
@Suppress("CompositionLocalAllowlist")
val LocalScreenNavigator = staticCompositionLocalOf<Navigator> {
    error("No Navigator provided")
}

// Use this snackbarhost to show messages on the main screen
@Suppress("CompositionLocalAllowlist")
val LocalRootSnackbarHostState = staticCompositionLocalOf<SnackbarHostState> {
    error("Root snackbarhost not provided")
}
