package com.foreverrafs.superdiary.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.request.ImageRequest
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import superdiary.shared_ui.generated.resources.Res
import superdiary.shared_ui.generated.resources.app_name
import superdiary.shared_ui.generated.resources.default_avatar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperDiaryAppBar(
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    onProfileClick: () -> Unit = {},
    avatarUrl: String? = null,
) {
    val model = ImageRequest
        .Builder(LocalPlatformContext.current)
        .data(avatarUrl)
        .build()

    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(Res.string.app_name),
                textAlign = TextAlign.Start,
                modifier = Modifier.semantics {
                    heading()
                },
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
            )
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        actions = {
            SubcomposeAsyncImage(
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable {
                        onProfileClick()
                    },
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
        },
        navigationIcon = {
            navigationIcon?.invoke()
        },
    )
}
