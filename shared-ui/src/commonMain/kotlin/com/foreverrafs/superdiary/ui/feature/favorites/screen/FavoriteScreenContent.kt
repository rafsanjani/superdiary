package com.foreverrafs.superdiary.ui.feature.favorites.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.ui.LocalRootSnackbarHostState
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryFilters
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryListActions
import com.foreverrafs.superdiary.ui.feature.diarylist.screen.DiaryList
import com.foreverrafs.superdiary.ui.feature.diarylist.screen.Empty

@Composable
fun FavoriteScreenContent(
    state: FavoriteScreenState,
    onToggleFavorite: suspend (diary: Diary) -> Boolean,
    modifier: Modifier = Modifier,
    onFavoriteClicked: (diary: Diary) -> Unit,
) {
    val snackbarHostState = LocalRootSnackbarHostState.current

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
            diaryListActions = DiaryListActions.Empty.copy(
                onDiaryClicked = onFavoriteClicked,
                onToggleFavorite = {
                    if (onToggleFavorite(it)) {
                        snackbarHostState.showSnackbar("Favorite removed")
                    }
                    true
                },
            ),
            snackbarHostState = SnackbarHostState(),
            emptyContent = {
                Text(
                    modifier = Modifier.padding(bottom = 64.dp),
                    text = "No favorite diary!",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 14.sp,
                )
            },
        )
    }
}
