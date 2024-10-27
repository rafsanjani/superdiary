package com.foreverrafs.superdiary.core.utils

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

actual typealias ActivityWrapper = ComponentActivity

@Composable
actual fun localActivityWrapper(): ActivityWrapper? {
    val context = LocalContext.current
    return context as? ComponentActivity
}
