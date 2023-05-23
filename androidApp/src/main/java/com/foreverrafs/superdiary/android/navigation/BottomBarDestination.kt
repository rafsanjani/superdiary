package com.foreverrafs.superdiary.android.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.ui.graphics.vector.ImageVector
import com.foreverrafs.superdiary.android.screens.destinations.CalendarScreenDestination
import com.foreverrafs.superdiary.android.screens.destinations.DiaryTimelineScreenDestination
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

@Suppress("unused")
enum class BottomBarDestination(
    val direction: DirectionDestinationSpec,
    val icon: ImageVector,
    val label: String,
) {
    Timeline(DiaryTimelineScreenDestination, Icons.Default.Timeline, "Timeline"),
    Calendar(CalendarScreenDestination, Icons.Default.CalendarMonth, "Calendar"),
}
