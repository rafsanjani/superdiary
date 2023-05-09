package com.foreverrafs.superdiary.android.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.foreverrafs.superdiary.android.AppTheme
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun CalendarScreen(modifier: Modifier = Modifier) {
    Content()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content() {
    Text(text = "Hello Calendar")
}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        Surface {
            Content()
        }
    }
}
