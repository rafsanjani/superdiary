package com.foreverrafs.auth

import androidx.compose.runtime.Composable

/**
 * A short-lived platform UI handle used only while starting interactive authentication.
 *
 * Implementations must not retain this beyond the authentication call.
 */
interface AuthUiContext

@Composable
expect fun rememberAuthUiContext(): AuthUiContext
