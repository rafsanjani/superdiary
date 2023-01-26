package com.foreverrafs.superdiary.android.components

import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.foreverrafs.superdiary.android.AppTheme

@Composable
fun SuperDiaryAppBar(modifier: Modifier = Modifier) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(text = "Super Diary ❤️")
        },
    )
}

@Composable
@Preview
private fun Preview() {
    AppTheme {
        SuperDiaryAppBar()
    }
}
