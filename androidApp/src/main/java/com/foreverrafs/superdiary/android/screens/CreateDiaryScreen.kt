package com.foreverrafs.superdiary.android.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.foreverrafs.superdiary.android.AppTheme
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun CreateDiaryScreen() {
    Content()
}

@Composable
private fun Content() {
    Column(modifier = Modifier.fillMaxSize()) {

    }
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
