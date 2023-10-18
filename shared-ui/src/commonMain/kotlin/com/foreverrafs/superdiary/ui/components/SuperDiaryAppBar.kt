package com.foreverrafs.superdiary.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperDiaryAppBar(
    modifier: Modifier = Modifier,
    showBackIcon: Boolean = false,
    onBackClicked: (() -> Unit)? = null,
) {
    TopAppBar(
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
        navigationIcon = {
            if (showBackIcon) {
                Icon(
                    modifier = Modifier
                        .clickable {
                            onBackClicked?.invoke()
                        }
                        .padding(8.dp),
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                )
            }
        },
        scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
    )
}
