package com.foreverrafs.superdiary.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperDiaryAppBar(
    modifier: Modifier = Modifier,
    navigationIcon: (@Composable () -> Unit)? = null,
    saveIcon: (@Composable () -> Unit)? = null,
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = "Super Diary",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth(),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
            )
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        actions = {
            saveIcon?.invoke()
        },
        navigationIcon = {
            navigationIcon?.invoke()
        },
    )
}

@Composable
fun NavigationIcon(onClick: (() -> Unit)) {
    Icon(
        modifier = Modifier
            .clip(CircleShape)
            .clickable {
                onClick()
            }
            .padding(8.dp),
        imageVector = Icons.Default.ArrowBackIosNew,
        contentDescription = null,
    )
}

@Composable
fun SaveIcon(onClick: (() -> Unit)) {
    Icon(
        modifier = Modifier
            .clip(CircleShape)
            .clickable {
                onClick()
            }
            .padding(8.dp),
        imageVector = Icons.Default.Check,
        contentDescription = null,
    )
}
