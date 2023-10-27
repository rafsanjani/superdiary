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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.foreverrafs.superdiary.ui.LocalRootSnackbarHostState
import com.foreverrafs.superdiary.ui.components.SuperDiaryAppBar
import com.foreverrafs.superdiary.ui.feature.calendar.CalendarScreen
import com.foreverrafs.superdiary.ui.feature.diarylist.screen.DiaryListScreen
import com.foreverrafs.superdiary.ui.feature.favorites.screen.FavoriteScreen

/**
 * Provides a navigation entry point for all the screens that rely on bottom tab for navigation
 */

object BottomNavigationScreen : Screen {
    @Composable
    override fun Content() {
        val snackbarHostState = LocalRootSnackbarHostState.current

        TabNavigator(DiaryListScreen) {
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                content = {
                    Surface(
                        modifier = Modifier.padding(it),
                        color = MaterialTheme.colorScheme.background,
                    ) {
                        CurrentTab()
                    }
                },
                topBar = { SuperDiaryAppBar() },
                bottomBar = {
                    NavigationBar {
                        TabNavigationItem(DiaryListScreen)
                        TabNavigationItem(CalendarScreen)
                        TabNavigationItem(FavoriteScreen)
                    }
                },
            )
        }
    }
}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current
    NavigationBarItem(
        selected = tabNavigator.current == tab,
        onClick = { tabNavigator.current = tab },
        icon = { Icon(painter = tab.options.icon!!, contentDescription = tab.options.title) },
    )
}
