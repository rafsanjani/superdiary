package com.foreverrafs.superdiary.design.components

import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import com.foreverrafs.superdiary.design.style.SuperDiaryPreviewTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import superdiary.design_system.generated.resources.Res
import superdiary.design_system.generated.resources.default_avatar

@Composable
fun SuperDiaryImage(
    url: String?,
    modifier: Modifier = Modifier,
) {
    val model = ImageRequest
        .Builder(LocalPlatformContext.current)
        .data(url)
        .build()

    val painter = rememberAsyncImagePainter(
        model = model,
        fallback = painterResource(Res.drawable.default_avatar),
        error = painterResource(Res.drawable.default_avatar),
        placeholder = painterResource(Res.drawable.default_avatar),
    )

    val colorFilter = if (painter.state.value is AsyncImagePainter.State.Success) {
        null
    } else {
        ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
    }

    Image(
        modifier = modifier,
        contentDescription = null,
        painter = painter,
        colorFilter = colorFilter,
    )
}

@Composable
@Preview
private fun Preview() {
    SuperDiaryPreviewTheme {
        SuperDiaryImage(
            url = null,
        )
    }
}

@Composable
@Preview
private fun PreviewDarkTheme() {
    SuperDiaryPreviewTheme(darkTheme = true) {
        SuperDiaryImage(
            url = null,
        )
    }
}
