package com.foreverrafs.superdiary.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.foreverrafs.superdiary.ui.style.SuperdiaryAppTheme

@Composable
internal fun TestAppContainer(content: @Composable () -> Unit) {
    SuperdiaryAppTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxSize(),
        ) {
            content()
        }
    }
}
