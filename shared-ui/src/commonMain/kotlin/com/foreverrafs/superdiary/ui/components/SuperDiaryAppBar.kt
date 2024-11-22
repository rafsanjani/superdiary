package com.foreverrafs.superdiary.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.foreverrafs.auth.model.UserInfo
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
    userInfo: UserInfo? = null,
) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalPlatformContext.current)
            .data(userInfo?.avatarUrl)
            .crossfade(true)
            .build(),
        error = painterResource(Res.drawable.default_avatar),
    )

    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(Res.string.app_name),
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .semantics {
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
            Icon(
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 4.dp)
                    .clip(CircleShape),
                painter = painter,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.inverseSurface,
            )
        },
        navigationIcon = {
            navigationIcon?.invoke()
        },
    )
}
