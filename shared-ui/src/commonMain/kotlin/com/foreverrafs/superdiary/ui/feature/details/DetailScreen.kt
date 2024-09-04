package com.foreverrafs.superdiary.ui.feature.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@Serializable
data class DetailScreen(val diaryId: String) {
    companion object {
        @Composable
        fun Content(
            diaryId: String,
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
                            navController.popBackStack()
                        },
                        onDeleteDiary = {
                            viewModel.deleteDiary(it)
                        },
                        viewState = state,
                    )
                }

                null -> {
                    // Nothing to do here
                }
            }
        }
    }
}
