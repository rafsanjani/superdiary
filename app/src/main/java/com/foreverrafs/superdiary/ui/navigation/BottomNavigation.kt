package com.foreverrafs.superdiary.ui.navigation

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import brand

@Composable
fun BottomNavigation(navController: NavController) {
    val destinations = listOf(
        BottomNavDestination.DiaryList,
        BottomNavDestination.AddDiary,
        BottomNavDestination.Calendar,
    )

    BottomNavigation(
        backgroundColor = MaterialTheme.colors.brand
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        for (destination in destinations) {
            BottomNavigationItem(
                selected = currentRoute == destination.route,
                icon = {
                    Icon(imageVector = destination.icon, contentDescription = null)
                },
                onClick = {
                    navController.navigate(route = destination.route)
                    {
                        navController.graph.startDestinationRoute?.let {
                            popUpTo(route = it) {
                                saveState = true
                            }
                        }

                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}