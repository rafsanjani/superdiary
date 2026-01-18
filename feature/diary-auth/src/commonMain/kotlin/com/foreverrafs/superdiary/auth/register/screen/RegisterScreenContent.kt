package com.foreverrafs.superdiary.auth.register.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.foreverrafs.superdiary.design.components.BrandLogo
import com.foreverrafs.superdiary.design.style.SuperDiaryPreviewTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import superdiary.feature.diary_auth.generated.resources.Res
import superdiary.feature.diary_auth.generated.resources.label_password
import superdiary.feature.diary_auth.generated.resources.label_register
import superdiary.feature.diary_auth.generated.resources.label_register_title
import superdiary.feature.diary_auth.generated.resources.logo

@Composable
internal fun RegisterScreenContent(
    viewState: RegisterScreenState,
    onRegisterClick: (name: String, username: String, password: String) -> Unit,
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val currentOnRegisterSuccess by rememberUpdatedState(onRegisterSuccess)

    var enableRegisterButton by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(viewState) {
        when (viewState) {
            is RegisterScreenState.Idle -> {
                enableRegisterButton = true
            }

            is RegisterScreenState.Processing -> {
                enableRegisterButton = false
            }

            is RegisterScreenState.Success -> {
                currentOnRegisterSuccess()
            }

            is RegisterScreenState.Error -> {
                enableRegisterButton = true
                snackbarHostState.showSnackbar(
                    message = viewState.error.message ?: "An error occurred",
                )
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Surface(
            modifier = Modifier.padding(padding).imePadding(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                var name by remember { mutableStateOf("") }
                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                var verifyPassword by remember { mutableStateOf("") }

                Spacer(modifier = Modifier.height(16.dp))

                BrandLogo(
                    modifier = Modifier.size(100.dp),
                    contentDescription = "brand logo",
                )

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = stringResource(Res.string.label_register_title),
                    style = MaterialTheme.typography.titleLarge,
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Name
                InputField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_name"),
                    label = "Full name",
                    value = name,
                    onValueChange = {
                        name = it
                    },
                    placeholder = "John Doe",
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Email
                InputField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_name"),
                    label = "Email",
                    value = email,
                    onValueChange = {
                        email = it
                    },
                    placeholder = "john@doe.com",
                )

                Spacer(modifier = Modifier.height(16.dp))

                InputField(
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

                Spacer(modifier = Modifier.height(20.dp))

                InputField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_password_reenter"),
                    label = "Re-enter password",
                    value = verifyPassword,
                    onValueChange = {
                        verifyPassword = it
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                )

                Spacer(modifier = Modifier.height(20.dp))

                RegisterButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("button_login"),
                    onClick = {
                        onRegisterClick(name, email, password)
                    },
                    enabled = enableRegisterButton,
                )

                Spacer(modifier = Modifier.height(44.dp))

                LoginText(onLoginClick = onLoginClick)
            }
        }
    }
}

@Composable
private fun RegisterButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Button(
        modifier = modifier
            .height(52.dp),
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        enabled = enabled,
    ) {
        Text(
            text = stringResource(Res.string.label_register),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
private fun LoginText(
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val registerText = buildAnnotatedString {
        withStyle(MaterialTheme.typography.bodyMedium.toSpanStyle()) {
            append("Already have an account? ")
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
                    tag = "login",
                    linkInteractionListener = {
                        onLoginClick()
                    },
                ),
            ) {
                append("Login")
            }
        }
    }

    Text(
        modifier = modifier,
        text = registerText,
    )
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
            maxLines = 1,
        )
    }
}

@Preview
@Composable
private fun Preview() {
    SuperDiaryPreviewTheme {
        RegisterScreenContent(
            viewState = RegisterScreenState.Idle,
            onRegisterClick = { name, username, password -> },
            onRegisterSuccess = {},
            onLoginClick = {},
        )
    }
}
