package com.foreverrafs.superdiary.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import com.foreverrafs.superdiary.ui.style.SuperdiaryTheme

@Suppress("ktlint:compose:modifier-missing-check")
@Composable
fun SuperdiaryPreviewTheme(
    modifier: Modifier = Modifier,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    SuperdiaryTheme(darkTheme = darkTheme) {
        Scaffold(
            modifier = modifier,
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                color = MaterialTheme.colorScheme.background,
            ) {
                CompositionLocalProvider(LocalInspectionMode provides true) {
                    content()
                }
            }
        }
    }
}
