package com.foreverrafs.superdiary.android.components

import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.foreverrafs.superdiary.android.AppTheme

@Composable
fun SuperDiaryAppBar(
    modifier: Modifier = Modifier,
    colors: TopAppBarColors = TopAppBarDefaults.mediumTopAppBarColors(),
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(text = "Super Diary ❤️")
        },
        colors = colors,
    )
}

@Composable
@Preview
private fun Preview() {
    AppTheme {
        SuperDiaryAppBar()
    }
}
