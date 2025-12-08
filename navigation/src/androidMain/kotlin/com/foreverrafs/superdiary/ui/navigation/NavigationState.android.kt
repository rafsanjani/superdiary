package com.foreverrafs.superdiary.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration

@Composable
actual fun rememberNavigationState(
    startRoute: NavKey,
    topLevelRoutes: Set<NavKey>,
    configuration: SavedStateConfiguration,
): NavigationState {
    val topLevelRoute = rememberSerializable(
        startRoute,
        topLevelRoutes,
        configuration = configuration,
    ) {
        mutableStateOf(startRoute)
    }

    val backStacks =
        topLevelRoutes.associateWith { key -> rememberNavBackStack(configuration, key) }

    return remember(startRoute, topLevelRoutes) {
        NavigationState(
            startRoute = startRoute,
            topLevelRoute = topLevelRoute,
            backStacks = backStacks,
        )
    }
}
