package com.foreverrafs.superdiary.ui.feature.dashboard

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreenContent() {
    Scaffold(modifier = Modifier.padding(8.dp)) {
        Column {
            Text(
                modifier = Modifier
                    .alpha(0.5f),
                text = "Progress",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
            )
            Box(
                modifier = Modifier
                    .size(150.dp).border(1.dp, Color.Black),
            ) {
            }
        }
    }
}
