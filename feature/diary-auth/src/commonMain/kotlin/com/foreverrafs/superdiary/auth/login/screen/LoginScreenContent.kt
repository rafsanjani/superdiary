package com.foreverrafs.superdiary.auth.login.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.foreverrafs.auth.NoCredentialsException
import com.foreverrafs.auth.model.UserInfo
import com.foreverrafs.superdiary.design.components.SuperDiaryButton
import com.foreverrafs.superdiary.design.components.SuperDiaryInputField
import com.foreverrafs.superdiary.design.style.SuperDiaryTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import superdiary.feature.diary_auth.generated.resources.Res
import superdiary.feature.diary_auth.generated.resources.google_icon
import superdiary.feature.diary_auth.generated.resources.label_google_button
import superdiary.feature.diary_auth.generated.resources.label_login
import superdiary.feature.diary_auth.generated.resources.label_login_title
import superdiary.feature.diary_auth.generated.resources.label_password
import superdiary.feature.diary_auth.generated.resources.label_register
import superdiary.feature.diary_auth.generated.resources.label_register_message
import superdiary.feature.diary_auth.generated.resources.label_username
import superdiary.feature.diary_auth.generated.resources.logo

@Composable
fun LoginScreenContent(
    viewState: LoginViewState,
    onSignInSuccess: (UserInfo) -> Unit,
    isFromDeeplink: Boolean,
    onLoginClick: (username: String, password: String) -> Unit,
    onLoginWithGoogle: () -> Unit,
    onRegisterClick: () -> Unit,
    onResetPasswordClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentOnSignInSuccess by rememberUpdatedState(onSignInSuccess)
    val snackbarHostState = remember { SnackbarHostState() }
    var enableLoginButton by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(viewState) {
        when (viewState) {
            is LoginViewState.Error -> {
                if (viewState.error is NoCredentialsException) {
                    snackbarHostState.showSnackbar(
                        viewState.error.message.orEmpty(),
                    )
                }

                enableLoginButton = true
            }

            is LoginViewState.Idle -> enableLoginButton = true
            is LoginViewState.Processing -> enableLoginButton = false

            is LoginViewState.Success -> currentOnSignInSuccess(viewState.userInfo)
        }
    }

    LaunchedEffect(isFromDeeplink) {
        if (isFromDeeplink) {
            snackbarHostState.showSnackbar("The link is invalid or has expired!")
        }
    }

    Scaffold(
        modifier = modifier,
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

                SuperDiaryInputField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_username"),
                    label = stringResource(Res.string.label_username),
                    value = username,
                    onValueChange = {
                        username = it
                    },
                    placeholder = "john.doe@gmail.com",
                )

                Spacer(modifier = Modifier.height(16.dp))

                SuperDiaryInputField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_password"),
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

                Spacer(modifier = Modifier.height(8.dp))

                ResetPasswordText(
                    modifier = Modifier.align(Alignment.End),
                    onResetPasswordClick = onResetPasswordClick,
                )

                Spacer(modifier = Modifier.height(8.dp))

                SuperDiaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("button_login"),
                    onClick = {
                        onLoginClick(username, password)
                    },
                    enabled = enableLoginButton,
                    text = stringResource(Res.string.label_login),
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
private fun ResetPasswordText(
    onResetPasswordClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        style = MaterialTheme.typography.bodyMedium,
        textDecoration = TextDecoration.Underline,
        fontWeight = FontWeight.Bold,
        modifier = modifier.clickable { onResetPasswordClick() },
        text = "Forgot password",
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
@Preview
private fun LoginPreview() {
    SuperDiaryTheme {
        LoginScreenContent(
            onLoginWithGoogle = {},
            onLoginClick = { _, _ -> },
            onRegisterClick = {},
            viewState = LoginViewState.Idle,
            onSignInSuccess = {},
            isFromDeeplink = false,
            onResetPasswordClick = {},
        )
    }
}
