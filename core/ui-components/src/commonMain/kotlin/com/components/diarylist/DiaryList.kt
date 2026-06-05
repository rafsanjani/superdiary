package com.components.diarylist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.utils.durationLabel
import kotlin.time.Clock
import kotlinx.coroutines.launch

/**
 * Display a list of diaries from the database. We have different functions
 * for adding and removing instead of just using the toggle. There are
 * instances where we just want to add entries whether they exist or
 * not and other times when we want to remove entries at all costs.
 *
 * @param diaries The list of diaries to display
 * @param inSelectionMode Whether we are actively selecting items or not
 * @param diaryFilters The filters that will be applied to the diary list
 * @param selectedIds The list of ids of the selected diary entries remove
 * @param diaryListActions Encapsulates all the actions that can be
 *    performed on a list of diaries.
 * @param onDeleteDiaries Delete the selected diaries from the list diaries
 * @param clock This is used to control the time/date for diary groupings
 * @param showSearchBar Determines whether the search/selection modifier
 *    bar will be shown. This is hidden in favorite screen.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiaryList(
    diaries: LazyPagingItems<Diary>,
    inSelectionMode: Boolean,
    diaryFilters: DiaryFilters,
    selectedIds: Set<Long>,
    diaryListActions: DiaryListActions,
    snackbarHostState: androidx.compose.material3.SnackbarHostState,
    onDeleteDiaries: (Set<Long>) -> Unit,
    modifier: Modifier = Modifier,
    clock: Clock = Clock.System,
    showSearchBar: Boolean = true,
    emptyContent: @Composable (() -> Unit)? = null,
    listState: LazyListState = rememberLazyListState(),
) {
    val coroutineScope = rememberCoroutineScope()
    val loadedDiaries = diaries.itemSnapshotList.items

    // When the user inputs a search query, we still want to show them
    // the search bar instead of the original empty screen
    if (diaries.itemCount > 0) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(space = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            state = listState,
        ) {
            items(
                count = diaries.itemCount,
                key = { index -> diaries.peek(index)?.id ?: "diary-placeholder-$index" },
            ) { index ->
                val diary = diaries[index] ?: return@items
                val date = diary.durationLabel(clock)
                val previousDiary = if (index > 0) diaries.peek(index - 1) else null
                val showHeader = previousDiary?.durationLabel(clock)?.label != date.label
                val groupDiaries = loadedDiaries.filter {
                    it.durationLabel(clock).label == date.label
                }

                if (showHeader) {
                    val isGroupSelected by remember(selectedIds, groupDiaries) {
                        mutableStateOf(selectedIds.containsAll(groupDiaries.mapNotNull { it.id }))
                    }

                    DiaryListHeader(
                        modifier = Modifier.animateItem(),
                        text = date.label,
                        inSelectionMode = inSelectionMode,
                        selected = isGroupSelected,
                        selectGroup = {
                            groupDiaries.forEach { groupDiary ->
                                groupDiary.id?.let(diaryListActions.onAddSelection)
                            }
                        },
                        deSelectGroup = {
                            groupDiaries.forEach { groupDiary ->
                                groupDiary.id?.let(diaryListActions.onRemoveSelection)
                            }
                        },
                    )
                }

                DiaryItem(
                    diary = diary,
                    selected = diary.id in selectedIds,
                    inSelectionMode = inSelectionMode,
                    modifier = Modifier.animateItem(
                        fadeInSpec = null,
                        fadeOutSpec = null,
                    )
                        .combinedClickable(
                            onClick = {
                                if (inSelectionMode) {
                                    diaryListActions.onToggleSelection(diary.id)
                                } else {
                                    diary.id?.let(diaryListActions.onDiaryClicked)
                                }
                            },
                            onLongClick = {
                                diaryListActions.onToggleSelection(diary.id)
                            },
                        ),
                    onToggleFavorite = {
                        coroutineScope.launch {
                            if (diaryListActions.onToggleFavorite(diary)) {
                                snackbarHostState.showSnackbar(
                                    message = "Favorite Updated!",
                                )
                            }
                        }
                    },
                )
            }
        }
    } else {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            if (emptyContent != null) {
                emptyContent()
            } else {
                Text(
                    modifier = Modifier.padding(bottom = 64.dp),
                    text = "No entry found!",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 14.sp,
                )
            }
        }
    }
}
