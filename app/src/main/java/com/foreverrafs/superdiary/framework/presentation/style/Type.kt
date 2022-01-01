import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.foreverrafs.superdiary.R

// Set of Material typography styles to start with
val MarkoFontFamily = FontFamily(
    fonts = listOf(
        Font(resId = R.font.marko_one, weight = FontWeight.Normal)
    )
)
val Typography = androidx.compose.material.Typography(
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    h4 = TextStyle(
        fontFamily = MarkoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp
    )
)
