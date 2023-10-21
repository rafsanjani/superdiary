package com.foreverrafs.superdiary.ui.feature.diarylist.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun SearchBar(
    modifier: Modifier = Modifier,
    onQueryChanged: (query: String) -> Unit,
    inSelectionMode: Boolean,
    onFilterClicked: () -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }

    val cornerRadius by animateDpAsState(
        if (isFocused) 8.dp else 4.dp,
    )

    val border by animateDpAsState(
        if (isFocused) 2.dp else Dp.Hairline,
    )

    LaunchedEffect(query) {
        onQueryChanged(query)
    }

    AnimatedVisibility(
        visible = inSelectionMode,
        modifier = modifier,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        TextField(
            modifier = modifier
                .onFocusChanged {
                    isFocused = it.hasFocus
                }
                .border(
                    width = border,
                    color = MaterialTheme.colorScheme.onBackground,
                    shape = RoundedCornerShape(cornerRadius),
                ),
            singleLine = true,
            value = query,
            onValueChange = { query = it },
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                )
            },
            trailingIcon = {
                Icon(
                    modifier = Modifier
                        .clickable { onFilterClicked() }
                        .padding(8.dp),
                    imageVector = Icons.Default.Sort,
                    contentDescription = null,
                )
            },
            shape = RoundedCornerShape(cornerRadius),
            placeholder = {
                Text(
                    modifier = Modifier.alpha(0.5f),
                    text = "Search in diaries",
                    style = MaterialTheme.typography.titleMedium,
                )
            },
            textStyle = MaterialTheme.typography.titleMedium,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search,
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                    focusManager.clearFocus(true)
                },
            ),
        )
    }
}
