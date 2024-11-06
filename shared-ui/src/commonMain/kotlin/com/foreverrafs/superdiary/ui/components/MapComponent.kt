package com.foreverrafs.superdiary.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.foreverrafs.superdiary.core.location.Location

@Composable
expect fun MapComponent(
    location: Location,
    modifier: Modifier = Modifier,
)
