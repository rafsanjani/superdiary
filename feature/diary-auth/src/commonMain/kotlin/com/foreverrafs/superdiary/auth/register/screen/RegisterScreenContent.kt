package com.foreverrafs.superdiary.auth.register.screen

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
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.foreverrafs.superdiary.design.components.BrandLogo
import com.foreverrafs.superdiary.design.components.PasswordInputField
import com.foreverrafs.superdiary.design.components.PrimaryButton
import com.foreverrafs.superdiary.design.components.SuperDiaryInputField
import com.foreverrafs.superdiary.design.style.SuperDiaryPreviewTheme
import org.jetbrains.compose.resources.stringResource
import superdiary.feature.diary_auth.generated.resources.Res
import superdiary.feature.diary_auth.generated.resources.error_email_required
import superdiary.feature.diary_auth.generated.resources.error_invalid_email
import superdiary.feature.diary_auth.generated.resources.error_name_required
import superdiary.feature.diary_auth.generated.resources.error_passwords_do_not_match
import superdiary.feature.diary_auth.generated.resources.error_password_required
import superdiary.feature.diary_auth.generated.resources.error_reenter_password_required
import superdiary.feature.diary_auth.generated.resources.label_already_have_account
import superdiary.feature.diary_auth.generated.resources.label_login_link
import superdiary.feature.diary_auth.generated.resources.label_name
import superdiary.feature.diary_auth.generated.resources.label_password
import superdiary.feature.diary_auth.generated.resources.label_register
import superdiary.feature.diary_auth.generated.resources.label_reenter_password
import superdiary.feature.diary_auth.generated.resources.label_register_title

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

    val name = rememberTextFieldState("")
    val email = rememberTextFieldState("")
    val password = rememberTextFieldState("")
    val verifyPassword = rememberTextFieldState("")

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var verifyPasswordError by remember { mutableStateOf<String?>(null) }

    var enableRegisterButton by remember { mutableStateOf(true) }

    // Resolve string resources once at composition time
    val strNameRequired = stringResource(Res.string.error_name_required)
    val strEmailRequired = stringResource(Res.string.error_email_required)
    val strInvalidEmail = stringResource(Res.string.error_invalid_email)
    val strPasswordRequired = stringResource(Res.string.error_password_required)
    val strReenterPasswordRequired = stringResource(Res.string.error_reenter_password_required)
    val strPasswordsDoNotMatch = stringResource(Res.string.error_passwords_do_not_match)

    LaunchedEffect(viewState) {
        when (viewState) {
            is RegisterScreenState.Idle -> {
                enableRegisterButton = true
                nameError = null
                emailError = null
                passwordError = null
                verifyPasswordError = null
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
                SuperDiaryInputField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_name"),
                    label = stringResource(Res.string.label_name),
                    state = name,
                    placeholder = "John Doe",
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                    ),
                    onValueChange = { nameError = null },
                    isError = nameError != null,
                    errorLabel = nameError,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Email
                SuperDiaryInputField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_email"),
                    label = "Email",
                    state = email,
                    placeholder = "john@doe.com",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                    ),
                    onValueChange = { emailError = null },
                    isError = emailError != null,
                    errorLabel = emailError,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password
                PasswordInputField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_password"),
                    label = stringResource(Res.string.label_password),
                    state = password,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next,
                    ),
                    onPasswordChange = { passwordError = null },
                    isError = passwordError != null,
                    errorLabel = passwordError,
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Verify password
                PasswordInputField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_password_reenter"),
                    label = stringResource(Res.string.label_reenter_password),
                    state = verifyPassword,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                    onPasswordChange = { verifyPasswordError = null },
                    isError = verifyPasswordError != null,
                    errorLabel = verifyPasswordError,
                )

                Spacer(modifier = Modifier.height(20.dp))

                PrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("button_register"),
                    onClick = {
                        var hasError = false

                        // Validate name
                        if (name.text.isBlank()) {
                            nameError = strNameRequired
                            hasError = true
                        } else {
                            nameError = null
                        }

                        // Validate email
                        if (email.text.isBlank()) {
                            emailError = strEmailRequired
                            hasError = true
                        } else if (!email.text.contains("@")) {
                            emailError = strInvalidEmail
                            hasError = true
                        } else {
                            emailError = null
                        }

                        // Validate password
                        if (password.text.isBlank()) {
                            passwordError = strPasswordRequired
                            hasError = true
                        } else {
                            passwordError = null
                        }

                        // Validate verify password
                        if (verifyPassword.text.isBlank()) {
                            verifyPasswordError = strReenterPasswordRequired
                            hasError = true
                        } else if (verifyPassword.text.toString() != password.text.toString()) {
                            verifyPasswordError = strPasswordsDoNotMatch
                            hasError = true
                        } else {
                            verifyPasswordError = null
                        }

                        if (!hasError) {
                            onRegisterClick(
                                name.text.toString(),
                                email.text.toString(),
                                password.text.toString(),
                            )
                        }
                    },
                    enabled = enableRegisterButton,
                    text = stringResource(Res.string.label_register),
                )

                Spacer(modifier = Modifier.height(44.dp))

                LoginText(onLoginClick = onLoginClick)
            }
        }
    }
}

@Composable
private fun LoginText(
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val prompt = stringResource(Res.string.label_already_have_account)
    val linkText = stringResource(Res.string.label_login_link)

    val registerText = buildAnnotatedString {
        withStyle(MaterialTheme.typography.bodyMedium.toSpanStyle()) {
            append(prompt)
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
                append(linkText)
            }
        }
    }

    Text(
        modifier = modifier,
        text = registerText,
    )
}

@Preview
@Composable
private fun Preview() {
    SuperDiaryPreviewTheme {
        RegisterScreenContent(
            viewState = RegisterScreenState.Idle,
            onRegisterClick = { _, _, _ -> },
            onRegisterSuccess = {},
            onLoginClick = {},
        )
    }
}
