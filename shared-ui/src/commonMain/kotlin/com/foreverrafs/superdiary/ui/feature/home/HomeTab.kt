package com.foreverrafs.superdiary.ui.feature.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryListScreen

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

    override val key: ScreenKey = uniqueScreenKey

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
