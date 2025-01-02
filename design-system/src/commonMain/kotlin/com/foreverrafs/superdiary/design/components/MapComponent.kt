package com.foreverrafs.superdiary.design.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun MapComponent(
    latitude: Double,
    longitude: Double,
    modifier: Modifier = Modifier,
)
