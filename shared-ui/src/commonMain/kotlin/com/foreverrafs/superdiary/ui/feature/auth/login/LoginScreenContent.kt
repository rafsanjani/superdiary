package com.foreverrafs.superdiary.ui.feature.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import superdiary.shared_ui.generated.resources.Res
import superdiary.shared_ui.generated.resources.google_icon
import superdiary.shared_ui.generated.resources.label_google_button
import superdiary.shared_ui.generated.resources.label_login
import superdiary.shared_ui.generated.resources.label_login_title
import superdiary.shared_ui.generated.resources.label_password
import superdiary.shared_ui.generated.resources.label_register
import superdiary.shared_ui.generated.resources.label_register_message
import superdiary.shared_ui.generated.resources.label_username
import superdiary.shared_ui.generated.resources.logo

@Suppress("ktlint:compose:modifier-missing-check")
@Composable
fun LoginScreenContent(
    viewState: LoginViewState,
    isTokenExpired: Boolean,
    onSignInSuccess: () -> Unit,
    onLoginClick: (username: String, password: String) -> Unit,
    onLoginWithGoogle: () -> Unit,
    onRegisterClick: () -> Unit,
) {
    val currentOnSignInSuccess by rememberUpdatedState(onSignInSuccess)
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        if (isTokenExpired) {
            snackbarHostState.showSnackbar("Session expired. Please log in again!")
        }
    }

    LaunchedEffect(viewState) {
        when (viewState) {
            is LoginViewState.Error -> {
                // no op
            }

            is LoginViewState.Idle -> {}
            is LoginViewState.Processing -> {}
            is LoginViewState.Success -> currentOnSignInSuccess()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                var username by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }

                Spacer(modifier = Modifier.height(40.dp))

                Image(
                    modifier = Modifier.size(100.dp),
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = "brand logo",
                )

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = stringResource(Res.string.label_login_title),
                    style = MaterialTheme.typography.titleLarge,
                )

                Spacer(modifier = Modifier.height(32.dp))

                InputField(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(Res.string.label_username),
                    value = username,
                    onValueChange = {
                        username = it
                    },
                    placeholder = "john.doe@gmail.com",
                )

                Spacer(modifier = Modifier.height(16.dp))

                InputField(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(Res.string.label_password),
                    value = password,
                    onValueChange = {
                        password = it
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                )

                Spacer(modifier = Modifier.height(20.dp))

                LoginButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onLoginClick(username, password)
                    },
                )

                Spacer(modifier = Modifier.height(44.dp))

                LoginDivider(modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(16.dp))

                GoogleButton(
                    onClick = onLoginWithGoogle,
                    modifier = Modifier
                        .fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(16.dp))

                RegisterText(
                    onRegisterClick = onRegisterClick,
                )
            }
        }
    }
}

@Composable
private fun LoginButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        modifier = modifier
            .height(52.dp),
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
    ) {
        Text(
            text = stringResource(Res.string.label_login),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
private fun RegisterText(
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val registerText = buildAnnotatedString {
        withStyle(MaterialTheme.typography.bodyMedium.toSpanStyle()) {
            append(stringResource(Res.string.label_register_message))
        }
        withStyle(
            MaterialTheme.typography.bodyMedium.toSpanStyle()
                .copy(
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                ),
        ) {
            withLink(
                LinkAnnotation.Clickable(
                    tag = "register",
                    linkInteractionListener = {
                        onRegisterClick()
                    },
                ),
            ) {
                append(stringResource(Res.string.label_register))
            }
        }
    }

    Text(
        modifier = modifier,
        text = registerText,
    )
}

@Composable
private fun GoogleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        modifier = modifier
            .height(52.dp),
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(
                30.dp,
                Alignment.CenterHorizontally,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(Res.drawable.google_icon),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
            )
            Text(
                text = stringResource(Res.string.label_google_button),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
private fun LoginDivider(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Left Divider
        HorizontalDivider(
            modifier = Modifier
                .weight(1f),
        )

        // Text
        Text(
            text = "OR",
            style = MaterialTheme.typography.labelMedium,
        )

        // Right Divider
        HorizontalDivider(
            modifier = Modifier
                .weight(1f),
        )
    }
}

@Composable
private fun InputField(
    label: String,
    value: String,
    onValueChange: (value: String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = label,
            textAlign = TextAlign.Start,
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = keyboardOptions,
            placeholder = {
                if (placeholder != null) {
                    Text(
                        text = placeholder,
                        modifier = Modifier.alpha(0.3f),
                    )
                }
            },
            visualTransformation = visualTransformation,
        )
    }
}
