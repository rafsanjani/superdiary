package com.foreverrafs.superdiary.ui.feature.favorites.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.foreverrafs.superdiary.ui.LocalScreenNavigator
import com.foreverrafs.superdiary.ui.feature.creatediary.screen.CreateDiaryScreen
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryFilters
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryListActions
import com.foreverrafs.superdiary.ui.feature.diarylist.screen.DiaryList
import com.foreverrafs.superdiary.ui.feature.diarylist.screen.Empty

@Composable
fun FavoriteScreenContent(state: FavoriteScreenState) {
    val navigator = LocalScreenNavigator.current

    if (state is FavoriteScreenState.Favorites) {
        DiaryList(
            modifier = Modifier.fillMaxSize(),
            diaries = state.diaries,
            inSelectionMode = false,
            diaryFilters = DiaryFilters(),
            selectedIds = setOf(),
            showSearchBar = false,
            onDeleteDiaries = {},
            onCancelSelection = {},
            diaryListActions = DiaryListActions.Empty.copy(
                onDiaryClicked = {
//                    navigator.push(CreateDiaryScreen(it))
                },
            ),
            snackbarHostState = SnackbarHostState(),
        )
    }
}
