package com.foreverrafs.superdiary.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import com.foreverrafs.superdiary.ui.style.SuperdiaryTheme

@Composable
fun SuperdiaryPreviewTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    SuperdiaryTheme(darkTheme = darkTheme) {
        CompositionLocalProvider(LocalInspectionMode provides true) {
            content()
        }
    }
}
