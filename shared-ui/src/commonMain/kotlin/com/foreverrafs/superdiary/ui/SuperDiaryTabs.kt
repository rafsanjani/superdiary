package com.foreverrafs.superdiary.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryListScreen
import com.foreverrafs.superdiary.ui.feature.favorites.FavoritesScreen

object CalendarTab : Tab {
    @Composable
    override fun Content() {
        Text("Calendars go here")
    }

    override val key: ScreenKey
        get() = "diary-list-calendar"

    override val options: TabOptions
        @Composable get() {
            val title = "Home"
            val icon = rememberVectorPainter(Icons.Default.CalendarMonth)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon,
                )
            }
        }
}

/**
 * The home tab provides the entry point for the diary list
 * and an option to create a new diary if the list is empty
 */
object HomeTab : Tab {

    @Composable
    override fun Content() {
        Navigator(
            screen = DiaryListScreen,
        ) { navigator ->
            SlideTransition(navigator)
        }
    }

    override val key: ScreenKey
        get() = "diary-list-destination"

    override val options: TabOptions
        @Composable get() {
            val title = "Home"
            val icon = rememberVectorPainter(Icons.Default.List)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon,
                )
            }
        }
}

object FavoritesTab : Tab {
    @Composable
    override fun Content() {
        FavoritesScreen()
    }

    override val key: ScreenKey
        get() = "diary-list-favorites"

    override val options: TabOptions
        @Composable get() {
            val title = "Home"
            val icon = rememberVectorPainter(Icons.Default.Favorite)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon,
                )
            }
        }
}