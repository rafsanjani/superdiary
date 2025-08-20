package com.foreverrafs.superdiary.auth.login.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties
import com.foreverrafs.superdiary.design.components.BasicMaterialDialog
import org.jetbrains.compose.resources.stringResource
import superdiary.feature.diary_auth.generated.resources.Res
import superdiary.feature.diary_auth.generated.resources.dialog_biometric_auth_error_message
import superdiary.feature.diary_auth.generated.resources.dialog_biometric_auth_error_negative_btn_text
import superdiary.feature.diary_auth.generated.resources.dialog_biometric_auth_error_positive_btn_text
import superdiary.feature.diary_auth.generated.resources.dialog_biometric_auth_error_title

@Composable
fun BiometricAuthErrorDialog(
    onExitApp: () -> Unit,
    onTryAgain: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    BasicMaterialDialog(
        onNegativeButton = onExitApp,
        onPositiveButton = onTryAgain,
        title = stringResource(Res.string.dialog_biometric_auth_error_title),
        message = stringResource(Res.string.dialog_biometric_auth_error_message),
        positiveButtonText = stringResource(Res.string.dialog_biometric_auth_error_positive_btn_text),
        negativeButtonText = stringResource(Res.string.dialog_biometric_auth_error_negative_btn_text),
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        ),
    )
}
