package com.foreverrafs.superdiary.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.readResourceBytes

@OptIn(InternalResourceApi::class)
@Composable
actual fun font(
    resource: String,
    weight: FontWeight,
    style: FontStyle,
): Font {
    return runBlocking {
        Font(
            identity = resource,
            data = readResourceBytes("font/$resource.ttf"),
            style = style,
            weight = weight,
        )
    }
}
