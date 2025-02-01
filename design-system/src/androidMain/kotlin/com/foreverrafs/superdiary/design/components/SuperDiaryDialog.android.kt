package com.foreverrafs.superdiary.design.components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import superdiary.design_system.generated.resources.Res
import superdiary.design_system.generated.resources.confirm_delete_diary_dialog_message
import superdiary.design_system.generated.resources.confirm_delete_diary_dialog_title
import superdiary.design_system.generated.resources.confirm_delete_diary_negative_button
import superdiary.design_system.generated.resources.confirm_delete_diary_positive_button
import superdiary.design_system.generated.resources.confirm_save_diary_dialog_message
import superdiary.design_system.generated.resources.confirm_save_diary_dialog_title
import superdiary.design_system.generated.resources.confirm_save_diary_negative_button
import superdiary.design_system.generated.resources.confirm_save_diary_positive_button

@Composable
actual fun ConfirmSaveDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    BasicMaterialDialog(
        onNegativeButton = onDismiss,
        onPositiveButton = onConfirm,
        title = stringResource(Res.string.confirm_save_diary_dialog_title),
        message = stringResource(Res.string.confirm_save_diary_dialog_message),
        positiveButtonText = stringResource(Res.string.confirm_save_diary_positive_button),
        negativeButtonText = stringResource(Res.string.confirm_save_diary_negative_button),
        onDismissRequest = onDismissRequest,
    )
}

@Composable
actual fun ConfirmDeleteDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    BasicMaterialDialog(
        onNegativeButton = onDismiss,
        onPositiveButton = onConfirm,
        title = stringResource(Res.string.confirm_delete_diary_dialog_title),
        message = stringResource(Res.string.confirm_delete_diary_dialog_message),
        negativeButtonText = stringResource(Res.string.confirm_delete_diary_negative_button),
        positiveButtonText = stringResource(Res.string.confirm_delete_diary_positive_button),
        onDismissRequest = onDismiss,
    )
}

@Composable
actual fun ConfirmBiometricAuthDialog(
    onDismiss: () -> Unit,
    onEnableBiometric: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    BasicMaterialDialog(
        onNegativeButton = onDismiss,
        onPositiveButton = onEnableBiometric,
        title = "Biometric Authentication",
        message = "Do you want to enable biometric authentication?",
        negativeButtonText = "No",
        positiveButtonText = "Yes",
        onDismissRequest = onDismissRequest,
    )
}
