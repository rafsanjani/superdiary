package com.foreverrafs.superdiary.list.presentation.detail.screen

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.foreverrafs.superdiary.list.presentation.detail.DetailsViewModel
import com.foreverrafs.superdiary.list.presentation.detail.DetailsViewState
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DetailScreen(
    diaryId: String,
    modifier: Modifier = Modifier,
) {
    val viewModel: DetailsViewModel = koinViewModel()
    val viewState by viewModel.detailsViewState.collectAsState()

    LaunchedEffect(diaryId) {
        viewModel.selectDiary(diaryId.toLong())
    }

    when (val state = viewState) {
        is DetailsViewState.DiarySelected -> {
            DetailScreenContent(
                modifier = modifier,
                viewState = state,
            )
        }

        null -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Selected account is null. This shouldn't ever happen.",
                )
            }
        }
    }
}
