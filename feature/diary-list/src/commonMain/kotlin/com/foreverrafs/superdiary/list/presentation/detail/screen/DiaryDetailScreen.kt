package com.foreverrafs.superdiary.list.presentation.detail.screen

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.foreverrafs.superdiary.design.components.BodyLargeText
import com.foreverrafs.superdiary.list.presentation.detail.DetailsViewModel
import com.foreverrafs.superdiary.list.presentation.detail.DetailsViewState
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import superdiary.feature.diary_list.generated.resources.Res
import superdiary.feature.diary_list.generated.resources.missing_selected_diary_message

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DiaryDetailScreen(
    diaryId: String,
    onProfileClick: () -> Unit,
    onBackPress: () -> Unit,
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
                onBackPress = onBackPress,
                onDeleteDiary = viewModel::deleteDiary,
                viewState = state,
                onProfileClick = onProfileClick,
            )
        }

        null -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                BodyLargeText(
                    text = stringResource(Res.string.missing_selected_diary_message),
                )
            }
        }
    }
}
