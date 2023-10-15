package com.foreverrafs.superdiary.ui.feature.calendar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions

object CalendarTab : Tab {
    @Composable
    override fun Content() {
        Text("Calendars go here")
    }

    override val key: ScreenKey = uniqueScreenKey

    override val options: TabOptions
        @Composable get() {
            val title = "Calendar"
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
