package com.foreverrafs.auth

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.fragment.app.FragmentActivity

class AndroidAuthUiContext(
    val activity: FragmentActivity,
) : AuthUiContext

@Composable
actual fun rememberAuthUiContext(): AuthUiContext {
    val activity = LocalActivity.current as? FragmentActivity
    requireNotNull(activity) { "Interactive authentication requires a FragmentActivity" }
    return remember(activity) { AndroidAuthUiContext(activity) }
}
