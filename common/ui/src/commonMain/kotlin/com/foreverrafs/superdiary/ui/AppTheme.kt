package com.foreverrafs.superdiary.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.fontFamilyResource
import superdiary.common.ui.MR

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            surfaceVariant = Color(0x27BDBDBD),
            surface = Color(0x27BDBDBD),
            background = Color(0xFF1E1E1E),
            secondaryContainer = Color(0x611E1E1E),
        )
    } else {
        lightColorScheme(
            secondaryContainer = Color.LightGray,
            background = Color(0xFFF5F5F5),
            surfaceVariant = Color(0x27BDBDBD),
            surface = Color(0x27BDBDBD),
        )
    }

    val typography = Typography(
        bodyMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
        ),
        displayLarge = TextStyle(
            fontFamily = fontFamilyResource(MR.fonts.Sofia.regular),
            fontWeight = FontWeight.Normal,
            fontSize = 30.sp,
        ),
        headlineMedium = TextStyle(
            fontFamily = fontFamilyResource(MR.fonts.SourceSans.bold),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            letterSpacing = (-0.3).sp,
        ),
    )
    val shapes = Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(4.dp),
        large = RoundedCornerShape(0.dp),
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = shapes,
        content = content,
    )
}
