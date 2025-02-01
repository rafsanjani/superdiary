package com.foreverrafs.superdiary.profile.presentation.screen

import androidx.compose.runtime.Composable

@Composable
expect fun ConfirmLogoutDialog(
    onLogout: () -> Unit,
    onDismiss: () -> Unit,
    onDismissRequest: () -> Unit,
)
