package com.foreverrafs.superdiary.design.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitViewController
import com.foreverrafs.superdiary.design.SwiftUIViewControllers

@Composable
actual fun MapComponent(
    latitude: Double,
    longitude: Double,
    modifier: Modifier,
) {
    UIKitViewController(
        factory = {
            SwiftUIViewControllers.GoogleMap(latitude, longitude)
        },
        modifier = modifier,
    )
}
