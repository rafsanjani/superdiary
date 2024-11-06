package com.foreverrafs.superdiary.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitViewController
import com.foreverrafs.superdiary.core.location.Location
import com.foreverrafs.superdiary.ui.SwiftUIViewControllers

@Composable
actual fun MapComponent(
    location: Location,
    modifier: Modifier,
) {
    UIKitViewController(
        factory = {
            SwiftUIViewControllers.GoogleMap(location)
        },
        modifier = modifier,
    )
}
