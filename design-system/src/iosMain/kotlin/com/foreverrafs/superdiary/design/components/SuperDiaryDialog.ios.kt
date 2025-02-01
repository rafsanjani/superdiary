package com.foreverrafs.superdiary.design.components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleCancel
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertActionStyleDestructive
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleAlert
import platform.UIKit.UIApplication
import superdiary.design_system.generated.resources.Res
import superdiary.design_system.generated.resources.confirm_delete_diary_dialog_message
import superdiary.design_system.generated.resources.confirm_delete_diary_dialog_title
import superdiary.design_system.generated.resources.confirm_delete_diary_negative_button
import superdiary.design_system.generated.resources.confirm_delete_diary_positive_button
import superdiary.design_system.generated.resources.confirm_save_diary_cancel_button
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
    BasicCupertinoDialog(
        onNegativeButton = onDismiss,
        onPositiveButton = onConfirm,
        title = stringResource(Res.string.confirm_save_diary_dialog_title),
        message = stringResource(Res.string.confirm_save_diary_dialog_message),
        positiveButtonText = stringResource(Res.string.confirm_save_diary_positive_button),
        negativeButtonText = stringResource(Res.string.confirm_save_diary_negative_button),
        cancelButtonText = stringResource(Res.string.confirm_save_diary_cancel_button),
        onDismissRequest = onDismissRequest,
    )
}

@Composable
fun BasicCupertinoDialog(
    title: String,
    message: String,
    positiveButtonText: String,
    negativeButtonText: String,
    onPositiveButton: () -> Unit,
    onNegativeButton: () -> Unit,
    onDismissRequest: () -> Unit,
    cancelButtonText: String? = null,
) {
    val alert = UIAlertController.alertControllerWithTitle(
        title = title,
        message = message,
        preferredStyle = UIAlertControllerStyleAlert,
    )

    alert.addAction(
        UIAlertAction.actionWithTitle(
            title = positiveButtonText,
            style = UIAlertActionStyleDefault,
            handler = {
                onPositiveButton()
            },
        ),
    )

    alert.addAction(
        UIAlertAction.actionWithTitle(
            title = negativeButtonText,
            style = UIAlertActionStyleDestructive,
            handler = {
                onNegativeButton()
            },
        ),
    )
    cancelButtonText?.let {
        alert.addAction(
            UIAlertAction.actionWithTitle(
                title = it,
                style = UIAlertActionStyleCancel,
                handler = { onDismissRequest() },
            ),
        )
    }

    // Get the top-most view controller
    val keyWindow = UIApplication.sharedApplication.keyWindow
    val rootViewController = keyWindow?.rootViewController
    rootViewController?.presentViewController(alert, animated = true, completion = null)
}

@Composable
actual fun ConfirmDeleteDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    BasicCupertinoDialog(
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
    BasicCupertinoDialog(
        onNegativeButton = onDismiss,
        onPositiveButton = onEnableBiometric,
        title = "Biometric Authentication",
        message = "Do you want to enable biometric authentication?",
        negativeButtonText = "No",
        positiveButtonText = "Yes",
        onDismissRequest = onDismissRequest,
    )
}
