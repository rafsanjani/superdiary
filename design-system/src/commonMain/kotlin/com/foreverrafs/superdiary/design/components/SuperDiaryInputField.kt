package com.foreverrafs.superdiary.design.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Deprecated("Please use the variant which accepts a TextFieldState")
@Composable
fun SuperDiaryInputField(
    label: String,
    value: String,
    onValueChange: (value: String) -> Unit,
    modifier: Modifier = Modifier,
    errorLabel: String? = null,
    placeholder: String? = null,
    readOnly: Boolean = false,
    isError: Boolean = false,
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
            readOnly = readOnly,
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
            isError = isError,
        )
        errorLabel?.let {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = it,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

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
                    if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (isPasswordVisible) "Show password" else "Hide password"

                Icon(
                    imageVector = visibilityIcon,
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
