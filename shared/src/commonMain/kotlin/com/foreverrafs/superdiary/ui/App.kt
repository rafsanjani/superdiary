package com.foreverrafs.superdiary.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.foreverrafs.superdiary.ui.components.SuperDiaryAppBar
import com.foreverrafs.superdiary.ui.screens.CalendarTab
import com.foreverrafs.superdiary.ui.screens.DiaryListTab

/**
 * Entry point into the whole app. In an ideal world
 * we'll only just render this composable on individual platforms
 * but the world isn't ideal, huh!
 */
@Composable
fun App() {
    TabNavigator(DiaryListTab) {
        AppTheme {
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        TabNavigationItem(DiaryListTab)
                        TabNavigationItem(CalendarTab)
                    }
                },
                topBar = { SuperDiaryAppBar() },
                content = {
                    Surface(
                        modifier = Modifier.fillMaxSize().padding(it),
                        color = MaterialTheme.colorScheme.background,
                    ) {
                        CurrentTab()
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
