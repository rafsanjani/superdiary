@file:Suppress("FunctionName", "Unused")

package com.foreverrafs.superdiary.ui

import androidx.compose.ui.uikit.OnFocusBehavior
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

class ViewController {
    fun mainViewController(): UIViewController = ComposeUIViewController(
        configure = {
            onFocusBehavior = OnFocusBehavior.DoNothing
        },
        content = { App() },
    )
}
