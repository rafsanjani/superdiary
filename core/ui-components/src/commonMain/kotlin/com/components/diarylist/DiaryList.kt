package com.components.diarylist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.utils.groupByDate
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
    diaries: List<Diary>,
    inSelectionMode: Boolean,
    diaryFilters: DiaryFilters,
    selectedIds: Set<Long>,
    diaryListActions: DiaryListActions,
    snackbarHostState: androidx.compose.material3.SnackbarHostState,
    onDeleteDiaries: (Set<Long>) -> Unit,
    onCancelSelection: () -> Unit,
    modifier: Modifier = Modifier,
    clock: Clock = Clock.System,
    showSearchBar: Boolean = true,
    emptyContent: @Composable (() -> Unit)? = null,
    listState: LazyListState = rememberLazyListState(),
) {
    val groupedDiaries =
        remember(diaries) {
            diaries.groupByDate(clock)
        }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier.padding(8.dp),
    ) {
        var showFilterDiariesBottomSheet by remember {
            mutableStateOf(false)
        }

        // Search and selection modifier bars
        if (showSearchBar) {
            Box {
                DiarySearchBar(
                    inSelectionMode = !inSelectionMode,
                    modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
                    onQueryChange = {
                        diaryListActions.onApplyFilters(diaryFilters.copy(entry = it))
                    },
                    onFilterClick = {
                        showFilterDiariesBottomSheet = true
                    },
                )

                DiarySelectionModifierBar(
                    inSelectionMode = inSelectionMode,
                    selectedIds = selectedIds,
                    onDelete = onDeleteDiaries,
                    onCancelSelection = onCancelSelection,
                )
            }
        }

        if (showFilterDiariesBottomSheet) {
            DiaryFilterSheet(
                onDismissRequest = {
                    showFilterDiariesBottomSheet = false
                },
                onApplyFilters = diaryListActions.onApplyFilters,
                diaryFilters = diaryFilters,
            )
        }

        // When the user inputs a search query, we still want to show them
        // the search bar instead of the original empty screen
        if (diaries.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(space = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                state = listState,
            ) {
                groupedDiaries.forEach { (date, diaries) ->
                    stickyHeader(key = date.label) {
                        val isGroupSelected by remember(selectedIds) {
                            mutableStateOf(selectedIds.containsAll(diaries.map { it.id }))
                        }

                        DiaryListHeader(
                            modifier = Modifier.animateItem(),
                            text = date.label,
                            inSelectionMode = inSelectionMode,
                            selected = isGroupSelected,
                            selectGroup = {
                                diaries.forEach { diary ->
                                    diary.id?.let(diaryListActions.onAddSelection)
                                }
                            },
                            deSelectGroup = {
                                diaries.forEach { diary ->
                                    diary.id?.let(diaryListActions.onRemoveSelection)
                                }
                            },
                        )
                    }

                    items(
                        items = diaries,
                        key = { item -> item.id ?: item.hashCode() },
                    ) { diary ->

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
            }
        } else {
            // When there is no diary entry from the search screen
            Box(
                modifier = Modifier.fillMaxSize(),
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
}
