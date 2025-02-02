package com.foreverrafs.superdiary.design.components

import androidx.compose.runtime.Composable

@Composable
actual fun BackHandler(onBack: () -> Unit) {
    androidx.activity.compose.BackHandler(
        enabled = true,
        onBack = onBack,
    )
}
