package com.foreverrafs.superdiary.android.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.foreverrafs.superdiary.android.AppTheme
import com.ramcosta.composedestinations.annotation.Destination

@AppNavGraph
@Destination
@Composable
fun DiaryListScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Green
    ) {
        Text(text = "Hello World")
    }
}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        DiaryListScreen()
    }
}
