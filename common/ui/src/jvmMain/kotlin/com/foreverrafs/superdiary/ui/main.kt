package com.foreverrafs.superdiary.ui

import androidx.compose.ui.window.singleWindowApplication
import com.foreverrafs.superdiary.ui.components.SuperDiaryAppBar

fun main() = singleWindowApplication {
    AppTheme {
        SuperDiaryAppBar()
    }
}
