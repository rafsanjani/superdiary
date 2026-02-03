package com.foreverrafs.superdiary.design.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.foreverrafs.superdiary.design.style.SuperDiaryPreviewTheme
import org.jetbrains.compose.resources.painterResource
import superdiary.design_system.generated.resources.Res
import superdiary.design_system.generated.resources.default_avatar

@Composable
fun Image(
    url: String?,
    modifier: Modifier = Modifier,
) {
    val model = ImageRequest
        .Builder(LocalPlatformContext.current)
        .crossfade(true)
        .data(url)
        .build()

    val painter = rememberAsyncImagePainter(
        model = model,
        fallback = painterResource(Res.drawable.default_avatar),
        error = painterResource(Res.drawable.default_avatar),
        placeholder = painterResource(Res.drawable.default_avatar),
        filterQuality = FilterQuality.High,
    )

    val loadingState by painter.state.collectAsStateWithLifecycle()

    Image(
        modifier = modifier,
        contentDescription = null,
        painter = painter,
        colorFilter = if (loadingState is AsyncImagePainter.State.Success) {
            null
        } else {
            ColorFilter.tint(
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
@Preview
private fun Preview() {
    SuperDiaryPreviewTheme {
        Image(
            url = null,
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
@Preview
private fun PreviewDarkTheme() {
    SuperDiaryPreviewTheme(darkTheme = true) {
        Image(
            url = null,
        )
    }
}
