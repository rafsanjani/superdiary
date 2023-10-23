package com.foreverrafs.superdiary.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.foreverrafs.superdiary.ui.components.SuperDiaryAppBar
import com.foreverrafs.superdiary.ui.style.SuperdiaryAppTheme

@Composable
internal fun TestAppContainer(content: @Composable () -> Unit) {
    SuperdiaryAppTheme {
        Scaffold(
            topBar = {
                SuperDiaryAppBar()
            },
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                color = MaterialTheme.colorScheme.background,
            ) {
                content()
            }
        }
    }
}
