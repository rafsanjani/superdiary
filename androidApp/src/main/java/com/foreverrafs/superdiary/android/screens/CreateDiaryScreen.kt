package com.foreverrafs.superdiary.android.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.foreverrafs.superdiary.android.AppTheme
import com.foreverrafs.superdiary.android.components.SuperDiaryAppBar
import com.ramcosta.composedestinations.annotation.Destination

@AppNavGraph(start = true)
@Destination
@Composable
fun CreateDiaryScreen(modifier: Modifier = Modifier) {
    AppTheme {
        Surface(modifier = modifier) {
            Content()
        }
    }
}

@Composable
private fun Content() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { SuperDiaryAppBar() }
    ) {
        Text(modifier = Modifier.padding(it), text = "Create Diary")
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
