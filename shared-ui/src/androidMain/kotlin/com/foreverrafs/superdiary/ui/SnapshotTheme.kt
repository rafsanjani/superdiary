package com.foreverrafs.superdiary.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import com.foreverrafs.superdiary.ui.style.SuperdiaryTheme
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.PreviewContextConfigurationEffect

@OptIn(ExperimentalResourceApi::class)
@Composable
fun SnapshotTheme(darkMode: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalInspectionMode provides true) {
        PreviewContextConfigurationEffect()
        SuperdiaryTheme(darkTheme = darkMode, content = content)
    }
}
