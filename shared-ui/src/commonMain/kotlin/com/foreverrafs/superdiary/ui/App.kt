package com.foreverrafs.superdiary.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import com.foreverrafs.superdiary.ui.home.BottomNavigationScreen
import com.foreverrafs.superdiary.ui.style.SuperdiaryAppTheme

/**
 * Entry point into the whole app. In an ideal world we'll only just render
 * this composable on individual platforms but the world isn't ideal, huh!
 */

@Composable
fun App() {
    SuperdiaryAppTheme {
        Navigator(BottomNavigationScreen) { navigator ->
            CompositionLocalProvider(LocalScreenNavigator provides navigator) {
                FadeTransition(
                    navigator = navigator,
                    animationSpec = tween(easing = LinearEasing),
                )
            }
        }
    }
}

/**
 * In voyager we are not really able to perform screen navigation from within a
 * tab navigator. LocalNavigator.push causes an exception because it expects a Tab
 * when used from within a TabNavigator. This way we are able to implicitly pass
 * the root navigator around to be used for navigation by individual tabs
 */
val LocalScreenNavigator = staticCompositionLocalOf<Navigator> {
    error("No Navigator provided")
}
