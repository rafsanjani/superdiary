import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import com.foreverrafs.superdiary.framework.presentation.style.colorPrimary

private val DarkColorPalette = darkColors(
    primary = colorPrimary,
)

private val LightColorPalette = lightColors(
    primary = colorPrimary
)

@Composable
fun SuperDiaryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // only use dark color palette for now
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = LightColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
