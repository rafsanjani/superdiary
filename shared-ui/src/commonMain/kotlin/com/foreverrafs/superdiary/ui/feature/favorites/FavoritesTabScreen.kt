package com.foreverrafs.superdiary.ui.feature.favorites

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryFilters
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryList
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryListActions
import com.foreverrafs.superdiary.ui.feature.diarylist.Empty

@Composable
fun FavoritesTabScreen(state: FavoritesTabScreenState) {
    if (state is FavoritesTabScreenState.Favorites) {
        DiaryList(
            modifier = Modifier.fillMaxSize(),
            diaries = state.diaries,
            inSelectionMode = false,
            diaryFilters = DiaryFilters(),
            selectedIds = setOf(),
            showSearchBar = false,
            onDeleteDiaries = {},
            onCancelSelection = {},
            diaryListActions = DiaryListActions.Empty,
            snackbarHostState = SnackbarHostState(),
        )
    }
}
