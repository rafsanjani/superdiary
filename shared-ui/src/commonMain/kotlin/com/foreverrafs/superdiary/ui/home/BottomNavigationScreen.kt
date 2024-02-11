package com.foreverrafs.superdiary.ui.home

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.foreverrafs.superdiary.ui.LocalRootSnackbarHostState
import com.foreverrafs.superdiary.ui.SuperDiaryTab
import com.foreverrafs.superdiary.ui.components.SuperDiaryAppBar
import com.foreverrafs.superdiary.ui.feature.dashboard.DashboardTab
import com.foreverrafs.superdiary.ui.feature.diarychat.DiaryChatTab
import com.foreverrafs.superdiary.ui.feature.favorites.screen.FavoriteTab

/**
 * Provides a navigation entry point for all the screens that rely on
 * bottom tab for navigation
 */

object BottomNavigationScreen : Screen {
    @Composable
    override fun Content() {
        val snackbarHostState = LocalRootSnackbarHostState.current

        TabNavigator(DashboardTab) {
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
                        BottomNavigationItem(DashboardTab)
                        BottomNavigationItem(FavoriteTab)
                        BottomNavigationItem(DiaryChatTab)
                    }
                },
            )
        }
    }

    @Composable
    private fun RowScope.BottomNavigationItem(tab: SuperDiaryTab) {
        val tabNavigator = LocalTabNavigator.current

        val selected = tabNavigator.current == tab
        NavigationBarItem(
            modifier = Modifier.testTag(tab.options.title),
            selected = selected,
            onClick = { tabNavigator.current = tab },
            icon = {
                Icon(
                    painter = if (selected) {
                        tab.selectedIcon
                    } else {
                        tab.options.icon
                            ?: rememberVectorPainter(Icons.Default.Home)
                    },
                    contentDescription = tab.options.title,
                )
            },
            label = {
                Text(
                    text = tab.options.title,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                )
            },
        )
    }
}
