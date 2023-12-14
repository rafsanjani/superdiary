package com.foreverrafs.superdiary.ui.home

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.foreverrafs.superdiary.ui.LocalRootSnackbarHostState
import com.foreverrafs.superdiary.ui.SuperDiaryScreen
import com.foreverrafs.superdiary.ui.components.SuperDiaryAppBar
import com.foreverrafs.superdiary.ui.feature.dashboard.DashboardScreen
import com.foreverrafs.superdiary.ui.feature.diarychat.DiaryChatScreen
import com.foreverrafs.superdiary.ui.feature.favorites.screen.FavoriteScreen

/**
 * Provides a navigation entry point for all the screens that rely on
 * bottom tab for navigation
 */

object BottomNavigationScreen : Screen {
    @Composable
    override fun Content() {
        val snackbarHostState = LocalRootSnackbarHostState.current

        TabNavigator(DashboardScreen) {
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                content = {
                    Surface(
                        modifier = Modifier.padding(it),
                        color = MaterialTheme.colorScheme.background,
                        content = { CurrentTab() },
                    )
                },
                topBar = { SuperDiaryAppBar() },
                bottomBar = {
                    NavigationBar {
                        TabNavigationItem(DashboardScreen)
                        TabNavigationItem(FavoriteScreen)
                        TabNavigationItem(DiaryChatScreen)
                    }
                },
            )
        }
    }
}

@Composable
private fun RowScope.TabNavigationItem(screen: SuperDiaryScreen) {
    val tabNavigator = LocalTabNavigator.current

    val selected = tabNavigator.current == screen
    NavigationBarItem(
        selected = selected,
        onClick = { tabNavigator.current = screen },
        icon = {
            Icon(
                painter = if (selected) screen.selectedIcon else screen.options.icon!!,
                contentDescription = screen.options.title,
            )
        },
        label = {
            Text(
                text = screen.options.title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            )
        },
    )
}
