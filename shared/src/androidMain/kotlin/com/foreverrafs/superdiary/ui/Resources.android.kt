package com.foreverrafs.superdiary.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

@Composable
actual fun font(
    name: String,
    resource: String,
    weight: FontWeight,
    style: FontStyle,
): Font {
    val context = LocalContext.current
    val id = context.resources.getIdentifier(resource, "font", context.packageName)
    return Font(id, weight, style)
}
