package com.foreverrafs.superdiary.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.foreverrafs.superdiary.ui.AppTheme

@Composable
fun ScreenContainer(content: @Composable () -> Unit) {
    AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}
