package com.foreverrafs.superdiary.favorite.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.foreverrafs.superdiary.design.components.AppBar
import com.foreverrafs.superdiary.domain.model.Diary

@Composable
fun FavoriteScreenContent(
    state: FavoriteScreenState,
    onToggleFavorite: suspend (Diary) -> Boolean,
    snackbarHostState: SnackbarHostState,
    avatarUrl: String? = null,
    onProfileClick: () -> Unit = {},
    onFavoriteClick: (diaryId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            AppBar(
                avatarUrl = avatarUrl,
                onProfileClick = onProfileClick,
                title = "Favorites",
            )
        },
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
}
