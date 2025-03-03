package com.foreverrafs.superdiary.ui.feature.details.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.foreverrafs.superdiary.ui.feature.details.DetailsViewModel
import com.foreverrafs.superdiary.ui.feature.details.DetailsViewState
import org.koin.compose.koinInject

@Composable
fun DetailScreen(
    diaryId: String,
    avatarUrl: String,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val viewModel: DetailsViewModel = koinInject()
    val viewState by viewModel.detailsViewState.collectAsState()

    LaunchedEffect(diaryId) {
        viewModel.initForDiary(diaryId.toLong())
    }

    when (val state = viewState) {
        is DetailsViewState.DiarySelected -> {
            DetailScreenContent(
                modifier = modifier,
                onNavigateBack = {
                    navController.navigateUp()
                },
                onDeleteDiary = {
                    viewModel.deleteDiary(it)
                },
                viewState = state,
                avatarUrl = avatarUrl,
            )
        }

        null -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "Selected account is null. This shouldn't ever happen.")
            }
        }
    }
}
