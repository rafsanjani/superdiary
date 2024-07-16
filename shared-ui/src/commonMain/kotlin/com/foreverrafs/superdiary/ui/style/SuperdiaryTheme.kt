package com.foreverrafs.superdiary.ui.style

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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import superdiary.shared_ui.generated.resources.Res
import superdiary.shared_ui.generated.resources.montserrat_alternatives_bold
import superdiary.shared_ui.generated.resources.montserrat_alternatives_regular
import superdiary.shared_ui.generated.resources.montserrat_alternatives_semibold
import superdiary.shared_ui.generated.resources.righteous_regular

@Composable
fun SuperdiaryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            surfaceVariant = Color(0xFF383838),
            background = Color(0xFF1E1E1E),
            secondaryContainer = Color(0x611E1E1E),
            tertiaryContainer = Color.DarkGray,
            primaryContainer = Color(0xFF303943),
        )
    } else {
        lightColorScheme(
            secondaryContainer = Color.LightGray,
            primaryContainer = Color(0xFFECEDFC),
            background = Color(0xFFF5F5F5),
            surfaceVariant = Color(0xFFECEEF1),
            tertiaryContainer = Color(0xffe3e3e3),
        )
    }

    val typography = Typography(
        bodyMedium = TextStyle(
            fontFamily = montserratAlternativesFontFamily(),
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
        ),
        displayLarge = TextStyle(
            fontFamily = righteousFontFamily(),
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp,
        ),
        displayMedium = TextStyle(
            fontFamily = montserratAlternativesFontFamily(),
            fontWeight = FontWeight.Light,
            fontSize = 20.sp,
        ),
        headlineMedium = TextStyle(
            fontFamily = montserratAlternativesFontFamily(),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            letterSpacing = (-0.3).sp,
        ),
        bodySmall = TextStyle(
            fontFamily = montserratAlternativesFontFamily(),
            fontSize = 16.sp,
        ),
        titleMedium = TextStyle(
            fontFamily = montserratAlternativesFontFamily(),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        labelMedium = TextStyle(
            fontFamily = montserratAlternativesFontFamily(),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        labelSmall = TextStyle(
            fontFamily = montserratAlternativesFontFamily(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
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

@Composable
fun righteousFontFamily(): FontFamily = FontFamily(
    listOf(
        Font(
            resource = Res.font.righteous_regular,
            weight = FontWeight.Normal,
            style = FontStyle.Normal,
        ),
    ),
)

@Composable
fun montserratAlternativesFontFamily(): FontFamily = FontFamily(
    listOf(
        Font(
            resource = Res.font.montserrat_alternatives_regular,
            weight = FontWeight.Normal,
            style = FontStyle.Normal,
        ),
        Font(
            resource = Res.font.montserrat_alternatives_bold,
            weight = FontWeight.Bold,
            style = FontStyle.Normal,
        ),
        Font(
            resource = Res.font.montserrat_alternatives_semibold,
            weight = FontWeight.Bold,
            style = FontStyle.Normal,
        ),
    ),
)
