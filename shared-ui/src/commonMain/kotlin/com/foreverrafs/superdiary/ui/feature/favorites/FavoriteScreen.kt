package com.foreverrafs.superdiary.ui.feature.favorites

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.foreverrafs.superdiary.ui.components.SuperDiaryAppBar

@Composable
fun FavoriteScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            SuperDiaryAppBar()
        },
    ) {
    }
}
