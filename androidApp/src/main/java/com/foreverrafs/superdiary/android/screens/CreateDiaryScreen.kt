package com.foreverrafs.superdiary.android.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.foreverrafs.superdiary.android.AppTheme
import com.foreverrafs.superdiary.android.screens.destinations.DiaryListScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@AppNavGraph(start = true)
@Destination
@Composable
fun CreateDiaryScreen(
    navigator: DestinationsNavigator
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                navigator.navigate(
                    DiaryListScreenDestination
                )
            },
        color = Color.Yellow
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
