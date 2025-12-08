package com.foreverrafs.superdiary.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration

@Composable
actual fun rememberNavigationState(
    startRoute: NavKey,
    topLevelRoutes: Set<NavKey>,
    configuration: SavedStateConfiguration,
): NavigationState {
    val topLevelRoute = remember(
        startRoute,
        topLevelRoutes,
    ) {
        mutableStateOf(startRoute)
    }

    val backStacks =
        topLevelRoutes.associateWith { key -> NavBackStack(elements = listOf(key).toTypedArray()) }

    return remember(startRoute, topLevelRoutes) {
        NavigationState(
            startRoute = startRoute,
            topLevelRoute = topLevelRoute,
            backStacks = backStacks,
        )
    }
}
