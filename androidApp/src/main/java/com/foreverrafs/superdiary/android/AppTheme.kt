package com.foreverrafs.superdiary.android

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foreverrafs.superdiary.android.style.PrincesSofia
import com.foreverrafs.superdiary.android.style.sourceSansPro
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
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
            fontFamily = PrincesSofia,
            fontWeight = FontWeight.Normal,
            fontSize = 30.sp,
        ),
        headlineMedium = TextStyle(
            fontFamily = sourceSansPro,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            letterSpacing = (-0.3).sp
        ),
    )
    val shapes = Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(4.dp),
        large = RoundedCornerShape(0.dp),
    )

    val systemUiController = rememberSystemUiController()
    val bgColor = colorScheme.background

    SideEffect {
        systemUiController.setStatusBarColor(
            color = bgColor
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = shapes,
        content = content,
    )
}
