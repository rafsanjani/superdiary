package com.foreverrafs.superdiary.favorite.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.components.diarylist.DiaryFilters
import com.components.diarylist.DiaryList
import com.components.diarylist.DiaryListActions
import com.foreverrafs.superdiary.domain.model.Diary

@Composable
fun FavoriteScreenContent(
    state: FavoriteScreenState,
    onToggleFavorite: suspend (Diary) -> Boolean,
    snackbarHostState: SnackbarHostState,
    onFavoriteClick: (diaryId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state is FavoriteScreenState.Content) {
        DiaryList(
            modifier = modifier.fillMaxSize(),
            diaries = state.diaries,
            inSelectionMode = false,
            diaryFilters = DiaryFilters(),
            selectedIds = setOf(),
            showSearchBar = false,
            onDeleteDiaries = {},
            onCancelSelection = {},
            diaryListActions = DiaryListActions(
                onDiaryClicked = onFavoriteClick,
                onToggleFavorite = {
                    if (onToggleFavorite(it)) {
                        snackbarHostState.showSnackbar("Favorite Removed")
                    }
                    true
                },
            ),
            snackbarHostState = SnackbarHostState(),
            emptyContent = {
                Text(
                    modifier = Modifier
                        .padding(bottom = 64.dp)
                        .testTag("empty_favorite_text"),
                    text = "No favorite diary!",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 14.sp,
                )
            },
        )
    }
}
