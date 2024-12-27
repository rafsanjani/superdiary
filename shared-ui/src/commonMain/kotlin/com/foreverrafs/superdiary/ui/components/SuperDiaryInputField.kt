package com.foreverrafs.superdiary.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

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
