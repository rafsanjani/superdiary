package com.foreverrafs.superdiary.ui.feature.calendar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.foreverrafs.superdiary.ui.SuperDiaryScreen

object CalendarScreen : SuperDiaryScreen() {
    @Composable
    override fun Content() {
        Text("Calendar Screen Content!")
    }

    override val selectedIcon: VectorPainter
        @Composable
        get() = rememberVectorPainter(Icons.Filled.CalendarMonth)

    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 2u,
            title = "Calendar",
            icon = rememberVectorPainter(Icons.Outlined.CalendarMonth),
        )
}
