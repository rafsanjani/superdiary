@file:Suppress("FunctionName", "Unused")

package com.foreverrafs.superdiary.ui

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.uikit.OnFocusBehavior
import androidx.compose.ui.window.ComposeUIViewController
import com.foreverrafs.superdiary.design.LocalNativeViewFactory
import com.foreverrafs.superdiary.design.NativeViewFactory
import platform.UIKit.UIViewController

@OptIn(ExperimentalComposeApi::class, ExperimentalComposeUiApi::class)
class ViewController(
    private val nativeViewFactory: NativeViewFactory,
) {
    fun mainViewController(): UIViewController = ComposeUIViewController(
        configure = {
            onFocusBehavior = OnFocusBehavior.DoNothing
        },
        content = {
            CompositionLocalProvider(LocalNativeViewFactory provides nativeViewFactory) {
                App()
            }
        },
    )
}
