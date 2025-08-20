package com.foreverrafs.superdiary.design.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitViewController
import com.foreverrafs.superdiary.design.LocalNativeViewFactory

@Composable
actual fun MapComponent(
    latitude: Double,
    longitude: Double,
    modifier: Modifier,
) {
    val nativeViewFactory = LocalNativeViewFactory.current

    UIKitViewController(
        factory = {
            nativeViewFactory.createGoogleMap(latitude, longitude)
        },
        modifier = modifier,
    )
}
