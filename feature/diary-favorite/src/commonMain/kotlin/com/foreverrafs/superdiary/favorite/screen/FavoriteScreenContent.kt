package com.foreverrafs.superdiary.favorite.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.collectAsLazyPagingItems
import com.components.diarylist.DiaryFilters
import com.components.diarylist.DiaryList
import com.components.diarylist.DiaryListActions
import com.foreverrafs.superdiary.design.components.AppBar
import com.foreverrafs.superdiary.design.components.TitleMediumText
import com.foreverrafs.superdiary.domain.model.Diary
import org.jetbrains.compose.resources.stringResource
import superdiary.feature.diary_favorite.generated.resources.Res
import superdiary.feature.diary_favorite.generated.resources.favorite_removed_message
import superdiary.feature.diary_favorite.generated.resources.favorites_screen_title
import superdiary.feature.diary_favorite.generated.resources.no_favorite_diary_message

@Composable
fun FavoriteScreenContent(
    state: FavoriteScreenState,
    onToggleFavorite: suspend (Diary) -> Boolean,
    snackbarHostState: SnackbarHostState,
    onFavoriteClick: (diaryId: Long) -> Unit,
    modifier: Modifier = Modifier,
    avatarUrl: String? = null,
    onProfileClick: () -> Unit = {},
) {
    val favoriteRemovedMessage = stringResource(Res.string.favorite_removed_message)

    Scaffold(
        modifier = modifier,
        topBar = {
            AppBar(
                avatarUrl = avatarUrl,
                onProfileClick = onProfileClick,
                title = stringResource(Res.string.favorites_screen_title),
            )
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = it),
        ) {
            if (state is FavoriteScreenState.Content) {
                val diaries = state.diaries.collectAsLazyPagingItems()
                DiaryList(
                    modifier = Modifier.fillMaxSize(),
                    diaries = diaries,
                    inSelectionMode = false,
                    diaryFilters = DiaryFilters(),
                    selectedIds = setOf(),
                    showSearchBar = false,
                    onDeleteDiaries = {},
                    diaryListActions = DiaryListActions(
                        onDiaryClicked = onFavoriteClick,
                        onToggleFavorite = {
                            if (onToggleFavorite(it)) {
                                snackbarHostState.showSnackbar(favoriteRemovedMessage)
                            }
                            true
                        },
                    ),
                    snackbarHostState = snackbarHostState,
                    emptyContent = {
                        TitleMediumText(
                            modifier = Modifier
                                .padding(bottom = 64.dp)
                                .testTag("empty_favorite_text"),
                            text = stringResource(Res.string.no_favorite_diary_message),
                            fontSize = 14.sp,
                        )
                    },
                )
            }
        }
    }
}
