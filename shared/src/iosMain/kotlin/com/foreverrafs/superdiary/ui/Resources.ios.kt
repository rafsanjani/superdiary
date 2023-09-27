package com.foreverrafs.superdiary.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.resource

@OptIn(ExperimentalResourceApi::class)
@Composable
actual fun font(
    name: String,
    resource: String,
    weight: FontWeight,
    style: FontStyle,
): Font {
    return runBlocking {
        Font(
            identity = resource,
            data = resource("font/$resource.ttf").readBytes(),
            style = style,
            weight = weight,
        )
    }
}
