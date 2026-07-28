package com.foreverrafs.superdiary.auth.changepassword.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.foreverrafs.superdiary.auth.changepassword.ChangePasswordViewModel
import com.foreverrafs.superdiary.auth.changepassword.ChangePasswordViewModel.ChangePasswordScreenAction
import com.foreverrafs.superdiary.auth.changepassword.PasswordStrength
import com.foreverrafs.superdiary.design.components.BodyLargeText
import com.foreverrafs.superdiary.design.components.BrandLogo
import com.foreverrafs.superdiary.design.components.DisplayMediumText
import com.foreverrafs.superdiary.design.components.PasswordInputField
import com.foreverrafs.superdiary.design.components.PrimaryButton
import com.foreverrafs.superdiary.design.style.SuperDiaryPreviewTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import superdiary.feature.diary_auth.generated.resources.Res
import superdiary.feature.diary_auth.generated.resources.change_password_error_message
import superdiary.feature.diary_auth.generated.resources.error_passwords_do_not_match
import superdiary.feature.diary_auth.generated.resources.logo
import superdiary.feature.diary_auth.generated.resources.new_password_label
import superdiary.feature.diary_auth.generated.resources.new_password_placeholder
import superdiary.feature.diary_auth.generated.resources.password_strength_label
import superdiary.feature.diary_auth.generated.resources.password_strength_medium
import superdiary.feature.diary_auth.generated.resources.password_strength_none
import superdiary.feature.diary_auth.generated.resources.password_strength_strong
import superdiary.feature.diary_auth.generated.resources.password_strength_weak
import superdiary.feature.diary_auth.generated.resources.repeat_password_label
import superdiary.feature.diary_auth.generated.resources.repeat_password_placeholder
import superdiary.feature.diary_auth.generated.resources.set_new_password_title
import superdiary.feature.diary_auth.generated.resources.update_password_button

@Composable
internal fun ChangePasswordScreenContent(
    onAction: (ChangePasswordScreenAction) -> Unit,
    onDismissErrorMessage: () -> Unit,
    viewState: ChangePasswordViewModel.ChangePasswordScreenState,
    onPasswordChangeSuccess: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentOnPasswordChangeSuccess by rememberUpdatedState(onPasswordChangeSuccess)
    val currentOnDismissErrorMessage by rememberUpdatedState(onDismissErrorMessage)
    val snackbarHostState = remember { SnackbarHostState() }
    val changePasswordErrorMessage = stringResource(Res.string.change_password_error_message)

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
    ) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(horizontal = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val passwordTextFieldState = rememberTextFieldState()
                val repeatPasswordTextFieldState = rememberTextFieldState()

                LaunchedEffect(viewState.isSuccess) {
                    viewState.isSuccess?.let { isSuccessful ->
                        if (isSuccessful) {
                            currentOnPasswordChangeSuccess()
                        }
                    }
                }

                LaunchedEffect(viewState.hasError) {
                    if (viewState.hasError) {
                        passwordTextFieldState.clearText()
                        repeatPasswordTextFieldState.clearText()

                        snackbarHostState.showSnackbar(message = changePasswordErrorMessage)
                        currentOnDismissErrorMessage()
                    }
                }

                Spacer(modifier = Modifier.height(54.dp))

                BrandLogo(
                    modifier = Modifier.size(72.dp),
                )

                Spacer(modifier = Modifier.height(24.dp))

                DisplayMediumText(
                    text = stringResource(Res.string.set_new_password_title),
                )

                Spacer(modifier = Modifier.height(48.dp))

                PasswordInputField(
                    label = stringResource(Res.string.new_password_label),
                    placeholder = stringResource(Res.string.new_password_placeholder),
                    onPasswordChange = {
                        onAction(ChangePasswordScreenAction.PasswordValueChange(it))
                    },
                    isError = viewState.arePasswordsMatching == false,
                    state = passwordTextFieldState,
                    enabled = viewState.isProcessing == false,
                )

                PasswordStrengthMeter(
                    strength = viewState.passwordStrength,
                    visible = viewState.passwordStrength != null,
                )

                Spacer(modifier = Modifier.height(20.dp))

                PasswordInputField(
                    label = stringResource(Res.string.repeat_password_label),
                    placeholder = stringResource(Res.string.repeat_password_placeholder),
                    onPasswordChange = {
                        onAction(ChangePasswordScreenAction.ConfirmPasswordValueChange(it))
                    },
                    errorLabel = if (viewState.arePasswordsMatching == false) {
                        stringResource(Res.string.error_passwords_do_not_match)
                    } else {
                        null
                    },
                    enabled = viewState.isProcessing == false,
                    isError = viewState.arePasswordsMatching == false,
                    state = repeatPasswordTextFieldState,
                )

                Spacer(modifier = Modifier.weight(1f))

                val isButtonEnabled = viewState.arePasswordsMatching == true &&
                    viewState.passwordStrength == PasswordStrength.Strong &&
                    viewState.isProcessing == false

                PrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    text = stringResource(Res.string.update_password_button),
                    enabled = isButtonEnabled,
                    onClick = {
                        onAction(ChangePasswordScreenAction.SubmitPasswordChange)
                    },
                )
            }
        }
    }
}

@Preview
@Composable
private fun ChangePasswordScreenContentPreview() {
    SuperDiaryPreviewTheme {
        ChangePasswordScreenContent(
            viewState = ChangePasswordViewModel.ChangePasswordScreenState(
                arePasswordsMatching = false,
            ),
            onAction = {},
            onPasswordChangeSuccess = {},
            onDismissErrorMessage = {},
        )
    }
}

@Composable
fun PasswordStrengthMeter(
    strength: PasswordStrength?,
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    // Use a box with a minimum height to prevent items from skipping around
    Box(modifier = modifier.sizeIn(minHeight = 24.dp)) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                strength?.let {
                    BodyLargeText(stringResource(Res.string.password_strength_label))

                    fun getColorForStrength(): Color = when (strength) {
                        PasswordStrength.None -> Color.White
                        PasswordStrength.Weak -> Color.Red
                        PasswordStrength.Medium -> Color.Yellow
                        PasswordStrength.Strong -> Color(0xff093509) // Dark Green
                    }

                    val strengthLabel = when (strength) {
                        PasswordStrength.None -> Res.string.password_strength_none
                        PasswordStrength.Weak -> Res.string.password_strength_weak
                        PasswordStrength.Medium -> Res.string.password_strength_medium
                        PasswordStrength.Strong -> Res.string.password_strength_strong
                    }
                    BodyLargeText(text = stringResource(strengthLabel), color = getColorForStrength())
                }
            }
        }
    }
}
