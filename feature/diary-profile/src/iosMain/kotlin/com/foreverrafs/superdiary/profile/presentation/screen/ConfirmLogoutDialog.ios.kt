package com.foreverrafs.superdiary.profile.presentation.screen

import androidx.compose.runtime.Composable
import com.foreverrafs.superdiary.design.components.BasicCupertinoDialog
import org.jetbrains.compose.resources.stringResource
import superdiary.feature.diary_profile.generated.resources.Res
import superdiary.feature.diary_profile.generated.resources.confirm_logout_dialog_cancel_button
import superdiary.feature.diary_profile.generated.resources.confirm_logout_dialog_confirm_button
import superdiary.feature.diary_profile.generated.resources.confirm_logout_dialog_message
import superdiary.feature.diary_profile.generated.resources.confirm_logout_dialog_title

@Composable
actual fun ConfirmLogoutDialog(
    onLogout: () -> Unit,
    onDismiss: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    BasicCupertinoDialog(
        onNegativeButton = onLogout,
        onPositiveButton = onDismiss,
        title = stringResource(Res.string.confirm_logout_dialog_title),
        message = stringResource(Res.string.confirm_logout_dialog_message),
        negativeButtonText = stringResource(Res.string.confirm_logout_dialog_confirm_button),
        positiveButtonText = stringResource(Res.string.confirm_logout_dialog_cancel_button),
        onDismissRequest = onDismissRequest,
    )
}
