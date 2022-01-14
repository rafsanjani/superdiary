import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import com.foreverrafs.superdiary.ui.style.Shapes
import com.foreverrafs.superdiary.ui.style.brandColorDark
import com.foreverrafs.superdiary.ui.style.brandColorLight

private val DarkColorPalette = darkColors(

)

private val LightColorPalette = lightColors(

)

val Colors.brand
    @Composable get() = if (isSystemInDarkTheme())
        brandColorDark else brandColorLight

@Composable
fun SuperDiaryTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    // only use dark color palette for now
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme
    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
