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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.foreverrafs.superdiary.ui.components.SuperDiaryButton
import com.foreverrafs.superdiary.ui.components.SuperDiaryInputField
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import superdiary.shared_ui.generated.resources.Res
import superdiary.shared_ui.generated.resources.logo

@Composable
fun SendPasswordResetEmailScreen(
    modifier: Modifier = Modifier,
) {
    val passwordResetViewModel: PasswordResetViewModel = koinViewModel()
    val viewState by passwordResetViewModel.viewState.collectAsStateWithLifecycle(
        initialValue = PasswordResetViewState(),
    )

    Scaffold(
        modifier = modifier,
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

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Reset your password",
                    style = MaterialTheme.typography.titleLarge,
                )

                Spacer(modifier = Modifier.height(32.dp))

                Crossfade(
                    targetState = viewState.isEmailSent,
                    animationSpec = tween(1000),
                ) { isEmailSent ->
                    if (isEmailSent == true) {
                        Column {
                            Text(
                                text = "A password reset email will be sent to the email address if it exists in our records.",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            SuperDiaryButton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("button_open_email"),
                                onClick = {
                                },
                                enabled = true,
                                text = "Open email",
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    } else {
                        Column {
                            SuperDiaryInputField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_email"),
                                label = "Email",
                                value = viewState.email,
                                onValueChange = passwordResetViewModel::onEmailChange,
                                placeholder = "john.doe@gmail.com",
                                isError = !viewState.isEmailValid,
                                errorLabel = viewState.inputErrorMessage,
                                readOnly = viewState.isLoading,
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            SuperDiaryButton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("button_reset_password"),
                                onClick = {
                                    passwordResetViewModel.onResetPassword()
                                },
                                enabled = viewState.isEmailValid && !viewState.isLoading,
                                text = "Reset password",
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}
