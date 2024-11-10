@file:Suppress("FunctionName", "Unused")

package com.foreverrafs.superdiary.ui

import androidx.compose.ui.uikit.OnFocusBehavior
import androidx.compose.ui.window.ComposeUIViewController
import com.foreverrafs.superdiary.core.location.Location
import platform.UIKit.UIViewController

class ViewController(
    googleMap: (location: Location) -> UIViewController,
) {
    init {
        SwiftUIViewControllers.GoogleMap = googleMap
    }

    fun mainViewController(): UIViewController = ComposeUIViewController(
        configure = {
            onFocusBehavior = OnFocusBehavior.DoNothing
        },
        content = { App() },
    )
}

object SwiftUIViewControllers {
    @Suppress("ktlint:standard:property-naming")
    lateinit var GoogleMap: (Location) -> UIViewController
}
