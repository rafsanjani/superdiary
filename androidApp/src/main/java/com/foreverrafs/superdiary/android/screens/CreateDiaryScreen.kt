package com.foreverrafs.superdiary.android.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.foreverrafs.superdiary.android.AppTheme
import com.foreverrafs.superdiary.android.components.SuperDiaryAppBar
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun CreateDiaryScreen(modifier: Modifier = Modifier) {
    AppTheme {
        Surface(modifier = modifier) {
            Content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { SuperDiaryAppBar(colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color.Green)) },
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
