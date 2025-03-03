@file:Suppress("FunctionName", "Unused")

package com.foreverrafs.superdiary.ui

import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.ui.uikit.OnFocusBehavior
import androidx.compose.ui.window.ComposeUIViewController
import com.foreverrafs.superdiary.design.SwiftUIViewControllers
import platform.UIKit.UIViewController

class ViewController(
    googleMap: (latitude: Double, longitude: Double) -> UIViewController,
) {
    init {
        SwiftUIViewControllers.GoogleMap = googleMap
    }

    @OptIn(ExperimentalComposeApi::class)
    fun mainViewController(): UIViewController = ComposeUIViewController(
        configure = {
            onFocusBehavior = OnFocusBehavior.DoNothing
            opaque = false
        },
        content = { App() },
    )
}
