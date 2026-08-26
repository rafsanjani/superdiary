package com.foreverrafs.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberAuthUiContext(): AuthUiContext = remember { object : AuthUiContext {} }
