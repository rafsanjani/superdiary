package com.foreverrafs.superdiary.design.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.foreverrafs.superdiary.design.style.LocalAnimatedContentScope
import com.foreverrafs.superdiary.design.style.LocalSharedTransitionScope
import com.foreverrafs.superdiary.design.style.SuperDiaryPreviewTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import superdiary.design_system.generated.resources.Res
import superdiary.design_system.generated.resources.app_name

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun AppBar(
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    onProfileClick: () -> Unit = {},
    avatarUrl: String? = null,
) {
    val animatedContentScope = LocalAnimatedContentScope.current
    val sharedTransitionScope = LocalSharedTransitionScope.current

    // workaround for https://issuetracker.google.com/issues/344343033
    var isPlaced by remember { mutableStateOf(false) }

    val appBarSharedElementModifier = with(sharedTransitionScope) {
        if (isPlaced) {
            Modifier.sharedElement(
                sharedContentState = sharedTransitionScope.rememberSharedContentState(
                    "app_name",
                ),
                animatedVisibilityScope = animatedContentScope,
            )
        } else {
            Modifier
        }
    }

    TopAppBar(
        modifier = modifier.onGloballyPositioned {
            isPlaced = true
        },
        title = {
            Text(
                text = stringResource(Res.string.app_name),
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .semantics {
                        heading()
                    }
                    .then(
                        appBarSharedElementModifier,
                    ),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        actions = {
            with(sharedTransitionScope) {
                Image(
                    modifier = Modifier
                        .sharedElement(
                            sharedContentState = sharedTransitionScope.rememberSharedContentState(
                                "profile_image",
                            ),
                            animatedVisibilityScope = animatedContentScope,
                        )
                        .padding(end = 4.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable {
                            onProfileClick()
                        },
                    url = avatarUrl,
                )
            }
        },
        navigationIcon = {
            with(sharedTransitionScope) {
                Box(
                    modifier = Modifier
                        .testTag("navigate_back_button")
                        .sharedElement(
                            sharedContentState = sharedTransitionScope.rememberSharedContentState("navigation_icon"),
                            animatedVisibilityScope = animatedContentScope,
                        ),
                ) {
                    navigationIcon?.invoke()
                }
            }
        },
    )
}

@Composable
fun SuperdiaryNavigationIcon(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    IconButton(
        modifier = modifier
            .testTag("navigate_back_button"),
        onClick = onClick,
    ) {
        Icon(
            modifier = Modifier
                .clip(CircleShape),
            imageVector = Icons.Default.ArrowBackIosNew,
            contentDescription = contentDescription,
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun SuperdiaryAppBarPreview() {
    SuperDiaryPreviewTheme(modifier = Modifier.fillMaxSize()) {
        SharedTransitionLayout {
            AppBar(
                onProfileClick = {},
            )
        }
    }
}
