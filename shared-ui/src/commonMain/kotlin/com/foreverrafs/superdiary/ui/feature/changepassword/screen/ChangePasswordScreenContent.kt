package com.foreverrafs.superdiary.ui.feature.changepassword.screen

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.foreverrafs.superdiary.design.components.PasswordInputField
import com.foreverrafs.superdiary.design.components.SuperDiaryButton
import com.foreverrafs.superdiary.design.style.SuperDiaryPreviewTheme
import com.foreverrafs.superdiary.ui.feature.changepassword.ChangePasswordViewModel
import com.foreverrafs.superdiary.ui.feature.changepassword.ChangePasswordViewModel.ChangePasswordScreenAction
import com.foreverrafs.superdiary.ui.feature.changepassword.PasswordStrength
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import superdiary.shared_ui.generated.resources.Res
import superdiary.shared_ui.generated.resources.logo

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

                LaunchedEffect(viewState.errorMessage) {
                    viewState.errorMessage?.let {
                        passwordTextFieldState.clearText()
                        repeatPasswordTextFieldState.clearText()

                        snackbarHostState.showSnackbar(message = it)
                        currentOnDismissErrorMessage()
                    }
                }

                Spacer(modifier = Modifier.height(54.dp))

                Image(
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier.size(72.dp),
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Set a new password",
                    style = MaterialTheme.typography.displayMedium,
                )

                Spacer(modifier = Modifier.height(48.dp))

                PasswordInputField(
                    label = "New Password",
                    placeholder = "Enter your new password",
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
                    label = "Repeat password",
                    placeholder = "Type your password again",
                    onPasswordChange = {
                        onAction(ChangePasswordScreenAction.ConfirmPasswordValueChange(it))
                    },
                    errorLabel = if (viewState.arePasswordsMatching == false) {
                        "Passwords do not match"
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

                SuperDiaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    text = "Update Password",
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
                    Text("Password strength:")

                    fun getColorForStrength(): Color = when (strength) {
                        PasswordStrength.None -> Color.White
                        PasswordStrength.Weak -> Color.Red
                        PasswordStrength.Medium -> Color.Yellow
                        PasswordStrength.Strong -> Color(0xff093509) // Dark Green
                    }

                    Text(text = strength.name, color = getColorForStrength())
                }
            }
        }
    }
}
