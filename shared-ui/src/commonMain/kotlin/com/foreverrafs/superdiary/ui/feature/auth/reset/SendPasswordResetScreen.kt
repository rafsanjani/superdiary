package com.foreverrafs.superdiary.ui.feature.auth.reset

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.foreverrafs.superdiary.ui.components.SuperDiaryButton
import com.foreverrafs.superdiary.ui.components.SuperDiaryInputField
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import superdiary.shared_ui.generated.resources.Res
import superdiary.shared_ui.generated.resources.label_open_email
import superdiary.shared_ui.generated.resources.label_reset_password_button
import superdiary.shared_ui.generated.resources.label_reset_password_header
import superdiary.shared_ui.generated.resources.logo

@Composable
fun SendPasswordResetEmailScreen(
    modifier: Modifier = Modifier,
) {
    val passwordResetViewModel: PasswordResetViewModel = koinViewModel()
    val viewState by passwordResetViewModel.viewState.collectAsStateWithLifecycle(
        initialValue = PasswordResetViewState(),
    )

    SendPasswordResetEmailScreenContent(
        modifier = modifier,
        viewState = viewState,
        onEmailChange = passwordResetViewModel::onEmailChange,
        onResetPasswordClick = passwordResetViewModel::onResetPassword,
        consumeTransientState = passwordResetViewModel::consumeTransientState,
    )
}

@Composable
fun SendPasswordResetEmailScreenContent(
    viewState: PasswordResetViewState,
    onEmailChange: (String) -> Unit,
    onResetPasswordClick: () -> Unit,
    consumeTransientState: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val currentConsumeTransientState by rememberUpdatedState(consumeTransientState)

    LaunchedEffect(viewState.isEmailSent) {
        if (viewState.isEmailSent != null && !viewState.isEmailSent) {
            snackbarHostState.showSnackbar("Error sending password reset email")
            currentConsumeTransientState()
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
    ) { padding ->
        Surface(
            modifier = Modifier.padding(padding),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                Image(
                    modifier = Modifier.size(100.dp),
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = "brand logo",
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(Res.string.label_reset_password_header),
                    style = MaterialTheme.typography.titleLarge,
                )

                Spacer(modifier = Modifier.height(24.dp))

                Crossfade(
                    targetState = viewState.isEmailSent,
                    animationSpec = tween(1000),
                ) { isEmailSent ->
                    if (isEmailSent == true) {
                        SuccessScreen(
                            email = viewState.email,
                        )
                    } else {
                        InputScreen(
                            viewState = viewState,
                            onEmailChange = onEmailChange,
                            onResetPasswordClick = onResetPasswordClick,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SuccessScreen(
    email: String,
) {
    Column {
        Image(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Success",
            modifier = Modifier
                .size(52.dp)
                .align(
                    Alignment.CenterHorizontally,
                ),
            colorFilter = ColorFilter.tint(
                MaterialTheme.colorScheme.onSurface,
            ),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = passwordResetEmailSuccessMessage(email),
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.weight(1f))

        SuperDiaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("button_open_email"),
            onClick = {},
            enabled = true,
            text = stringResource(Res.string.label_open_email, email),
        )
        Spacer(
            modifier = Modifier.height(16.dp),
        )
    }
}

@Composable
fun passwordResetEmailSuccessMessage(email: String) = buildAnnotatedString {
    withStyle(MaterialTheme.typography.bodyMedium.toSpanStyle()) {
        append("Detailed instructions on how to restore your account will be sent to you at ")
    }

    withStyle(
        MaterialTheme.typography.bodyMedium.toSpanStyle().copy(
            fontWeight = FontWeight.Bold,
        ),
    ) {
        append(email)
    }

    withStyle(MaterialTheme.typography.bodyMedium.toSpanStyle()) {
        append("\n\n")
        append("If you did not receive the email, please check your spam/junk folder.")
    }
}

@Composable
private fun InputScreen(
    viewState: PasswordResetViewState,
    onEmailChange: (String) -> Unit,
    onResetPasswordClick: () -> Unit,
) {
    Column {
        SuperDiaryInputField(
            modifier = Modifier.fillMaxWidth().testTag("input_email"),
            label = "Email",
            value = viewState.email,
            onValueChange = onEmailChange,
            placeholder = "john.doe@gmail.com",
            isError = !viewState.isEmailValid,
            errorLabel = viewState.inputErrorMessage,
            readOnly = viewState.isLoading,
        )

        Spacer(modifier = Modifier.weight(1f))

        SuperDiaryButton(
            modifier = Modifier.fillMaxWidth().testTag("button_reset_password"),
            onClick = onResetPasswordClick,
            enabled = viewState.isEmailValid && !viewState.isLoading,
            text = stringResource(Res.string.label_reset_password_button),
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}
