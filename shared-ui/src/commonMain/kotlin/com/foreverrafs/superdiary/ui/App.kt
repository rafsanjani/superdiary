package com.foreverrafs.superdiary.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.foreverrafs.superdiary.ui.components.SuperDiaryAppBar
import com.foreverrafs.superdiary.ui.feature.calendar.CalendarTab
import com.foreverrafs.superdiary.ui.feature.favorites.FavoritesTab
import com.foreverrafs.superdiary.ui.feature.home.HomeTab
import com.foreverrafs.superdiary.ui.style.SuperdiaryAppTheme

/**
 * Entry point into the whole app. In an ideal world we'll only just render
 * this composable on individual platforms but the world isn't ideal, huh!
 */
@Composable
fun App() {
    val tabs = listOf(
        HomeTab,
        CalendarTab,
        FavoritesTab,
    )

    val snackbarHostState = remember { SnackbarHostState() }

    TabNavigator(
        tab = tabs.first { it is HomeTab },
    ) {
        SuperdiaryAppTheme {
            Scaffold(
                topBar = {
                    SuperDiaryAppBar()
                },
                bottomBar = {
                    NavigationBar {
                        tabs.forEach {
                            TabNavigationItem(it)
                        }
                    }
                },
                snackbarHost = {
                    SnackbarHost(snackbarHostState)
                },
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
                        CurrentTab()
                    }
                }
            }
        }
    }
}

// This isn't a good idea but so far there isn't any clean way of passing non-serializable
// arguments to the Screens
val LocalSnackbarHostState = staticCompositionLocalOf { SnackbarHostState() }

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current

    NavigationBarItem(
        selected = tabNavigator.current == tab,
        onClick = { tabNavigator.current = tab },
        icon = {
            tab.options.icon?.let {
                Icon(
                    painter = it,
                    contentDescription = tab.options.title,
                )
            }
        },
    )
}
