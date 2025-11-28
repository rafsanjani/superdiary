package com.foreverrafs.superdiary.design.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecureTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldLabelPosition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import org.jetbrains.compose.resources.painterResource
import superdiary.design_system.generated.resources.Res
import superdiary.design_system.generated.resources.ic_visibility
import superdiary.design_system.generated.resources.ic_visibility_off

@Composable
fun SuperDiaryInputField(
    label: String,
    state: TextFieldState,
    modifier: Modifier = Modifier,
    onValueChange: (value: String) -> Unit = {},
    errorLabel: String? = null,
    placeholder: String? = null,
    readOnly: Boolean = false,
    isError: Boolean = false,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    val text by snapshotFlow { state.text }.collectAsState(initial = state.text)
    val currentOnTextChanged by rememberUpdatedState(onValueChange)

    LaunchedEffect(text) {
        currentOnTextChanged(text.toString())
    }

    TextField(
        modifier = modifier
            .fillMaxWidth(),
        state = state,
        isError = isError,
        placeholder = {
            if (placeholder != null) {
                Text(
                    text = placeholder,
                    modifier = Modifier.alpha(0.3f).fillMaxWidth(),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        },
        lineLimits = lineLimits,
        labelPosition = TextFieldLabelPosition.Above(alignment = Alignment.Start),
        keyboardOptions = keyboardOptions,
        readOnly = readOnly,
        supportingText = {
            errorLabel?.let {
                Text(it)
            }
        },
        label = {
            Text(
                text = label,
                modifier = Modifier.fillMaxWidth(),
            )
        },
    )
}

@Composable
fun PasswordInputField(
    label: String,
    state: TextFieldState,
    modifier: Modifier = Modifier,
    onPasswordChange: (String) -> Unit = {},
    enabled: Boolean = true,
    errorLabel: String? = null,
    placeholder: String? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    val text by snapshotFlow { state.text }.collectAsState(initial = state.text)

    val currentOnTextChanged by rememberUpdatedState(onPasswordChange)

    LaunchedEffect(text) {
        currentOnTextChanged(text.toString())
    }

    SecureTextField(
        modifier = modifier
            .fillMaxWidth(),
        state = state,
        placeholder = {
            placeholder?.let {
                Text(
                    text = placeholder,
                    modifier = Modifier.alpha(0.3f).fillMaxWidth(),
                )
            }
        },
        isError = isError,
        keyboardOptions = keyboardOptions,
        label = {
            Text(
                text = label,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        labelPosition = TextFieldLabelPosition.Above(alignment = Alignment.Start),
        supportingText = {
            errorLabel?.let {
                Text(it)
            }
        },
        trailingIcon = {
            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                val visibilityIcon =
                    if (isPasswordVisible) Res.drawable.ic_visibility else Res.drawable.ic_visibility_off

                val description = if (isPasswordVisible) "Show password" else "Hide password"

                Icon(
                    painter = painterResource(visibilityIcon),
                    contentDescription = description,
                )
            }
        },
        textObfuscationMode = if (isPasswordVisible) {
            TextObfuscationMode.Visible
        } else {
            TextObfuscationMode.RevealLastTyped
        },
        enabled = enabled,
    )
}
