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
import com.foreverrafs.superdiary.auth.register.FieldValidationError
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
import superdiary.feature.diary_auth.generated.resources.error_password_required
import superdiary.feature.diary_auth.generated.resources.error_passwords_do_not_match
import superdiary.feature.diary_auth.generated.resources.error_reenter_password_required
import superdiary.feature.diary_auth.generated.resources.label_already_have_account
import superdiary.feature.diary_auth.generated.resources.label_login_link
import superdiary.feature.diary_auth.generated.resources.label_name
import superdiary.feature.diary_auth.generated.resources.label_password
import superdiary.feature.diary_auth.generated.resources.label_reenter_password
import superdiary.feature.diary_auth.generated.resources.label_register
import superdiary.feature.diary_auth.generated.resources.label_register_title

@Composable
internal fun RegisterScreenContent(
    viewState: RegisterScreenState,
    onRegisterClick: (name: String, username: String, password: String, verifyPassword: String) -> Unit,
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit,
    onFieldChange: () -> Unit,
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

    // Resolve error strings inline, just before their use in LaunchedEffect.
    val errorNameRequired = stringResource(Res.string.error_name_required)
    val errorEmailRequired = stringResource(Res.string.error_email_required)
    val errorInvalidEmail = stringResource(Res.string.error_invalid_email)
    val errorPasswordRequired = stringResource(Res.string.error_password_required)
    val errorReenterPasswordRequired = stringResource(Res.string.error_reenter_password_required)
    val errorPasswordsDoNotMatch = stringResource(Res.string.error_passwords_do_not_match)

    LaunchedEffect(viewState) {
        when (viewState) {
            is RegisterScreenState.Idle -> {
                enableRegisterButton = true
            }

            is RegisterScreenState.Processing -> {
                enableRegisterButton = false
                nameError = null
                emailError = null
                passwordError = null
                verifyPasswordError = null
            }

            is RegisterScreenState.ValidationError -> {
                enableRegisterButton = true
                viewState.errors.let { errors ->
                    nameError = errors.nameError.toErrorMessage(
                        required = errorNameRequired,
                        invalidEmail = null,
                        passwordsDoNotMatch = null,
                    )
                    emailError = errors.emailError.toErrorMessage(
                        required = errorEmailRequired,
                        invalidEmail = errorInvalidEmail,
                        passwordsDoNotMatch = null,
                    )
                    passwordError = errors.passwordError.toErrorMessage(
                        required = errorPasswordRequired,
                        invalidEmail = null,
                        passwordsDoNotMatch = null,
                    )
                    verifyPasswordError = errors.verifyPasswordError.toErrorMessage(
                        required = errorReenterPasswordRequired,
                        invalidEmail = null,
                        passwordsDoNotMatch = errorPasswordsDoNotMatch,
                    )
                }
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
                    onValueChange = {
                        nameError = null
                        onFieldChange()
                    },
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
                    onValueChange = {
                        emailError = null
                        onFieldChange()
                    },
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
                    onPasswordChange = {
                        passwordError = null
                        onFieldChange()
                    },
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
                    onPasswordChange = {
                        verifyPasswordError = null
                        onFieldChange()
                    },
                    isError = verifyPasswordError != null,
                    errorLabel = verifyPasswordError,
                )

                Spacer(modifier = Modifier.height(20.dp))

                PrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("button_register"),
                    onClick = {
                        onRegisterClick(
                            name.text.toString(),
                            email.text.toString(),
                            password.text.toString(),
                            verifyPassword.text.toString(),
                        )
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

/**
 * Maps a [FieldValidationError] to its localized string for display below the input field.
 *
 * Each field has different error messages for the same error code
 * (e.g. "Name is required" vs "Email is required"), so the caller provides
 * the messages for the specific field via the named parameters.
 */
private fun FieldValidationError?.toErrorMessage(
    required: String?,
    invalidEmail: String?,
    passwordsDoNotMatch: String?,
): String? = when (this) {
    FieldValidationError.Required -> required
    FieldValidationError.InvalidEmail -> invalidEmail
    FieldValidationError.PasswordsDoNotMatch -> passwordsDoNotMatch
    null -> null
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
            onRegisterClick = { _, _, _, _ -> },
            onRegisterSuccess = {},
            onLoginClick = {},
            onFieldChange = {},
        )
    }
}
