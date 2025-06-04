package com.foreverrafs.superdiary.design.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import org.jetbrains.compose.resources.painterResource
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

    Image(
        modifier = modifier,
        contentDescription = null,
        painter = rememberAsyncImagePainter(
            model = model,
            fallback = painterResource(Res.drawable.default_avatar),
        ),
    )

//    SubcomposeAsyncImage(
//        modifier = modifier,
//        model = model,
//        contentDescription = null,
//        content = {
//            val state by painter.state.collectAsState()
//            when (state) {
//                is AsyncImagePainter.State.Empty,
//                is AsyncImagePainter.State.Error,
//                is AsyncImagePainter.State.Loading,
//                -> {
//                    Image(
//                        contentDescription = null,
//                        painter = painterResource(Res.drawable.default_avatar),
//                        colorFilter = ColorFilter.tint(
//                            color = MaterialTheme.colorScheme.onSurface,
//                        ),
//                    )
//                }
//
//                is AsyncImagePainter.State.Success -> SubcomposeAsyncImageContent()
//            }
//        },
//    )
}
