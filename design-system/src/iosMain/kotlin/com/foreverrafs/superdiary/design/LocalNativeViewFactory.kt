package com.foreverrafs.superdiary.design

import androidx.compose.runtime.compositionLocalOf

val LocalNativeViewFactory = compositionLocalOf<NativeViewFactory> {
    error("LocalNativeViewFactory not provided")
}
