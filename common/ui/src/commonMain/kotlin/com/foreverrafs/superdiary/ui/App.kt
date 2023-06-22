package com.foreverrafs.superdiary.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun App() {
    Surface(modifier = Modifier.fillMaxSize(), color = Color.Green) {
        Box(contentAlignment = Alignment.Center) {
            Button(onClick = {}) {
                Text("Hello Compose")
            }
        }
    }
}