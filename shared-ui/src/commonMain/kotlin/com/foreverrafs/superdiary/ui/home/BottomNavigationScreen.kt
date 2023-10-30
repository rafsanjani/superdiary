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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.foreverrafs.superdiary.ui.LocalRootSnackbarHostState
import com.foreverrafs.superdiary.ui.components.SuperDiaryAppBar
import com.foreverrafs.superdiary.ui.feature.calendar.CalendarScreen
import com.foreverrafs.superdiary.ui.feature.diaryai.DiaryAiScreen
import com.foreverrafs.superdiary.ui.feature.diarylist.screen.DiaryListScreen
import com.foreverrafs.superdiary.ui.feature.favorites.screen.FavoriteScreen
import com.foreverrafs.superdiary.ui.SuperDiaryScreen

/**
 * Provides a navigation entry point for all the screens that rely on
 * bottom tab for navigation
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
                        TabNavigationItem(DiaryAiScreen)
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
            )
        },
    )
}
