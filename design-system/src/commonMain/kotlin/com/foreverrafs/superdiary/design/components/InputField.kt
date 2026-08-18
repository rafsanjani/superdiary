package com.foreverrafs.superdiary.design.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecureTextField
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import superdiary.design_system.generated.resources.Res
import superdiary.design_system.generated.resources.content_description_hide_password
import superdiary.design_system.generated.resources.content_description_show_password
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
    pill: Boolean = false,
) {
    val text by snapshotFlow { state.text }.collectAsState(initial = state.text)
    val currentOnTextChanged by rememberUpdatedState(onValueChange)
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    LaunchedEffect(text) {
        currentOnTextChanged(text.toString())
    }

    TextField(
        modifier = modifier.fillMaxWidth(),
        state = state,
        isError = isError,
        placeholder = {
            if (placeholder != null && (!pill || !isFocused)) {
                BodyLargeText(
                    text = placeholder,
                    modifier = Modifier
                        .alpha(if (pill) 0.55f else 0.3f)
                        .fillMaxWidth(),
                )
            }
        },
        lineLimits = lineLimits,
        labelPosition = TextFieldLabelPosition.Above(alignment = Alignment.Start),
        keyboardOptions = keyboardOptions,
        readOnly = readOnly,
        supportingText = {
            errorLabel?.let {
                BodySmallText(it)
            }
        },
        label = {
            BodySmallText(
                text = label,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        shape = if (pill) RoundedCornerShape(32.dp) else TextFieldDefaults.shape,
        colors = if (pill) pillTextFieldColors() else TextFieldDefaults.colors(),
        interactionSource = interactionSource,
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
    pill: Boolean = false,
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    val text by snapshotFlow { state.text }.collectAsState(initial = state.text)

    val currentOnTextChanged by rememberUpdatedState(onPasswordChange)
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    LaunchedEffect(text) {
        currentOnTextChanged(text.toString())
    }

    SecureTextField(
        modifier = modifier.fillMaxWidth(),
        state = state,
        placeholder = {
            if (placeholder != null && (!pill || !isFocused)) {
                BodyLargeText(
                    text = placeholder,
                    modifier = Modifier
                        .alpha(if (pill) 0.55f else 0.3f)
                        .fillMaxWidth(),
                )
            }
        },
        isError = isError,
        keyboardOptions = keyboardOptions,
        label = {
            BodySmallText(
                text = label,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        labelPosition = TextFieldLabelPosition.Above(alignment = Alignment.Start),
        supportingText = {
            errorLabel?.let {
                BodySmallText(it)
            }
        },
        trailingIcon = {
            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                val visibilityIcon =
                    if (isPasswordVisible) Res.drawable.ic_visibility else Res.drawable.ic_visibility_off

                val description = stringResource(
                    if (isPasswordVisible) {
                        Res.string.content_description_hide_password
                    } else {
                        Res.string.content_description_show_password
                    },
                )

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
        shape = if (pill) RoundedCornerShape(32.dp) else TextFieldDefaults.shape,
        colors = if (pill) pillTextFieldColors() else TextFieldDefaults.colors(),
        interactionSource = interactionSource,
    )
}

@Composable
private fun pillTextFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
    errorContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    cursorColor = MaterialTheme.colorScheme.onSurface,
    errorCursorColor = MaterialTheme.colorScheme.error,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    disabledIndicatorColor = Color.Transparent,
    errorIndicatorColor = MaterialTheme.colorScheme.error,
    focusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
    unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
)
