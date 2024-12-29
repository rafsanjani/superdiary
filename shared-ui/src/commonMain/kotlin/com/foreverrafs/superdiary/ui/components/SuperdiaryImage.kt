package com.foreverrafs.superdiary.ui.components

import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.request.ImageRequest
import org.jetbrains.compose.resources.painterResource
import superdiary.shared_ui.generated.resources.Res
import superdiary.shared_ui.generated.resources.default_avatar

@Composable
fun SuperdiaryImage(
    url: String?,
    modifier: Modifier = Modifier,
) {
    val model = ImageRequest
        .Builder(LocalPlatformContext.current)
        .data(url)
        .build()

    SubcomposeAsyncImage(
        modifier = modifier,
        model = model,
        contentDescription = null,
        content = {
            val state by painter.state.collectAsState()
            when (state) {
                is AsyncImagePainter.State.Empty,
                is AsyncImagePainter.State.Error,
                is AsyncImagePainter.State.Loading,
                -> {
                    Image(
                        contentDescription = null,
                        painter = painterResource(Res.drawable.default_avatar),
                        colorFilter = ColorFilter.tint(
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                    )
                }

                is AsyncImagePainter.State.Success -> SubcomposeAsyncImageContent()
            }
        },
    )
}
