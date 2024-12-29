package com.foreverrafs.superdiary.ui.feature.diarychat.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.foreverrafs.superdiary.ui.feature.diarychat.DiaryChatViewModel
import org.koin.compose.koinInject

@Composable
fun DiaryChatTab() {
    val screenModel: DiaryChatViewModel = koinInject()
    val screenState by screenModel.viewState.collectAsState()

    DiaryChatScreenContent(
        screenState = screenState,
        onQueryDiaries = screenModel::queryDiaries,
    )
}
