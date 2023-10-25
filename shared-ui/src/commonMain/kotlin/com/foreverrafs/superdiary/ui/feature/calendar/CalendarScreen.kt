package com.foreverrafs.superdiary.ui.feature.calendar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions

object CalendarScreen : Tab {
    @Composable
    override fun Content() {
        Text("Calendar Screen Content!")
    }

    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 2u,
            title = "Calendar",
            icon = rememberVectorPainter(Icons.Default.CalendarMonth),
        )
}
