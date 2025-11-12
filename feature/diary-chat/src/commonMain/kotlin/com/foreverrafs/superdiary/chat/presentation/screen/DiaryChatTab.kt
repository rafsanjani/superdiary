package com.foreverrafs.superdiary.chat.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.foreverrafs.superdiary.chat.presentation.DiaryChatViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DiaryChatTab() {
    val screenModel: DiaryChatViewModel = koinViewModel()
    val screenState by screenModel.viewState.collectAsState()

    DiaryChatScreenContent(
        screenState = screenState,
        onQueryDiaries = screenModel::queryDiaries,
    )
}
