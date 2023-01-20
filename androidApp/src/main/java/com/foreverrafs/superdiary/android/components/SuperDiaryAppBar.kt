package com.foreverrafs.superdiary.android.components

import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.foreverrafs.superdiary.android.AppTheme

@Composable
fun SuperDiaryAppBar() {
    TopAppBar(
        title = {
            Text(text = "Super Diary ❤️")
        }
    )
}

@Composable
@Preview
private fun Preview() {
    AppTheme {
        SuperDiaryAppBar()
    }
}