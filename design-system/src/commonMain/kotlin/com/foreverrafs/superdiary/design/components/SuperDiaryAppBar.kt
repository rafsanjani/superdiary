package com.foreverrafs.superdiary.design.components

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import superdiary.design_system.generated.resources.Res
import superdiary.design_system.generated.resources.app_name

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperDiaryAppBar(
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    onProfileClick: () -> Unit = {},
    avatarUrl: String? = null,
) {
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
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        actions = {
            SuperDiaryImage(
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable {
                        onProfileClick()
                    },
                url = avatarUrl,
            )
        },
        navigationIcon = {
            navigationIcon?.invoke()
        },
    )
}
