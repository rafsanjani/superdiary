package com.foreverrafs.superdiary.ui.feature.diarylist

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.utils.groupByDate
import com.foreverrafs.superdiary.ui.components.ConfirmDeleteDialog
import com.foreverrafs.superdiary.ui.components.SuperDiaryAppBar
import com.foreverrafs.superdiary.ui.feature.diarylist.components.DiaryFilterSheet
import com.foreverrafs.superdiary.ui.feature.diarylist.components.DiaryHeader
import com.foreverrafs.superdiary.ui.feature.diarylist.components.DiarySearchBar
import com.foreverrafs.superdiary.ui.feature.diarylist.components.DiarySelectionModifierBar
import com.foreverrafs.superdiary.ui.format
import com.foreverrafs.superdiary.ui.style.montserratAlternativesFontFamily
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun DiaryListScreen(
    state: DiaryListScreenState,
    modifier: Modifier = Modifier,
    onAddEntry: () -> Unit,
    onApplyFilters: (filters: DiaryFilters) -> Unit,
    onDeleteDiaries: (selectedIds: List<Diary>) -> Unit,
    diaryFilters: DiaryFilters,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
    ) {
        SuperDiaryAppBar()

        when (state) {
            is DiaryListScreenState.Content -> {
                var selectedIds by rememberSaveable {
                    mutableStateOf(emptySet<Long>())
                }

                var showConfirmDeleteDialog by remember {
                    mutableStateOf(false)
                }

                val coroutineScope = rememberCoroutineScope()

                if (showConfirmDeleteDialog) {
                    ConfirmDeleteDialog(
                        onDismiss = {
                            showConfirmDeleteDialog = false
                        },
                        onConfirm = {
                            onDeleteDiaries(
                                state.diaries.filter { selectedIds.contains(it.id) },
                            )

                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    "${selectedIds.size + 1} item(s) deleted!",
                                )
                            }

                            selectedIds = emptySet()
                            showConfirmDeleteDialog = false
                        },
                    )
                }

                DiaryListContent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    diaries = state.diaries,
                    onAddEntry = onAddEntry,
                    onApplyFilters = onApplyFilters,
                    diaryFilters = diaryFilters,
                    isFiltered = state.filtered,
                    onDeleteDiaries = {
                        showConfirmDeleteDialog = true
                    },
                    onRemoveSelection = { diaryId ->
                        diaryId?.let {
                            selectedIds = selectedIds.minus(diaryId)
                        }
                    },
                    onAddSelection = { diaryId ->
                        diaryId?.let {
                            selectedIds = selectedIds.plus(diaryId)
                        }
                    },
                    onToggleSelection = { diaryId ->
                        selectedIds = selectedIds.addOrRemove(diaryId)
                    },
                    selectedIds = selectedIds,
                    onCancelSelection = {
                        selectedIds = setOf()
                    },
                )

                SnackbarHost(
                    hostState = snackbarHostState,
                )
            }

            is DiaryListScreenState.Error -> ErrorContent(modifier = Modifier.fillMaxSize())

            is DiaryListScreenState.Loading -> LoadingContent(modifier = Modifier.fillMaxSize())
        }
    }
}

/**
 * Display a list of diaries from the database. We have different functions
 * for adding and removing instead of just using the toggl. There are
 * instances where we just want to add entries whether they exist or
 * not and other times where we want to remove entries at all costs.
 *
 * @param diaries The list of diaries to display
 * @param inSelectionMode Whether we are actively selecting items or not
 * @param diaryFilters The filters that will be applied to the diary list
 * @param selectedIds The list of ids of the selected diary entries
 * @param onAddEntry Add a new entry to the list
 * @param onAddSelection Add an entry to the list of selected items
 * @param onRemoveSelection Remove an entry from the list of selected items
 * @param onToggleSelection Add an entry to the list of selected items or
 *     remove it otherwise.
 * @param onDeleteDiaries Delete the selected diaries from the list
 * @param onApplyFilters Apply the selected filters onto the list of
 *     diaries
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiaryList(
    modifier: Modifier = Modifier,
    diaries: List<Diary>,
    inSelectionMode: Boolean,
    diaryFilters: DiaryFilters,
    selectedIds: Set<Long>,
    onAddEntry: () -> Unit,
    onAddSelection: (id: Long?) -> Unit,
    onRemoveSelection: (id: Long?) -> Unit,
    onToggleSelection: (id: Long?) -> Unit,
    onDeleteDiaries: (selectedIds: List<Long>) -> Unit,
    onCancelSelection: () -> Unit,
    onApplyFilters: (filters: DiaryFilters) -> Unit,
) {
    val groupedDiaries = remember(diaries) {
        diaries.groupByDate()
    }

    Column(
        modifier = modifier
            .padding(8.dp),
    ) {
        var showFilterDiariesBottomSheet by remember {
            mutableStateOf(false)
        }

        // Search and selection modifier bars
        Box {
            DiarySearchBar(
                inSelectionMode = !inSelectionMode,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                onQueryChanged = {
                    onApplyFilters(diaryFilters.copy(entry = it))
                },
                onFilterClicked = {
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

        if (showFilterDiariesBottomSheet) {
            DiaryFilterSheet(
                onDismissRequest = {
                    showFilterDiariesBottomSheet = false
                },
                onApplyFilters = {
                    onApplyFilters(
                        diaryFilters.copy(
                            date = it.date,
                            sort = it.sort,
                        ),
                    )
                },
                diaryFilters = diaryFilters,
            )
        }

        // When the user inputs a search query, we still want to show them
        // the search bar instead of the original empty screen
        if (diaries.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(space = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                state = rememberLazyListState(),
            ) {
                groupedDiaries.forEach { (date, diaries) ->
                    stickyHeader(key = date.label) {
                        val isGroupSelected by remember(selectedIds) {
                            mutableStateOf(selectedIds.containsAll(diaries.map { it.id }))
                        }

                        DiaryHeader(
                            modifier = Modifier.animateItemPlacement(),
                            text = date.label,
                            inSelectionMode = inSelectionMode,
                            selected = isGroupSelected,
                            selectGroup = {
                                diaries.forEach { diary ->
                                    diary.id?.let {
                                        onAddSelection(it)
                                    }
                                }
                            },
                            deSelectGroup = {
                                diaries.forEach { diary ->
                                    diary.id?.let {
                                        onRemoveSelection(it)
                                    }
                                }
                            },
                        )
                    }

                    items(
                        items = diaries.sortedByDescending { it.id },
                        key = { item -> item.id.toString() },
                    ) { diary ->
                        DiaryItem(
                            modifier = Modifier
                                .animateItemPlacement()
                                .combinedClickable(
                                    onClick = {
                                        if (inSelectionMode) {
                                            onToggleSelection(diary.id)
                                        } else {
                                            // Process regular click here
                                        }
                                    },
                                    onLongClick = {
                                        onToggleSelection(diary.id)
                                    },
                                ),
                            diary = diary,
                            selected = diary.id in selectedIds,
                            inSelectionMode = inSelectionMode,
                        )
                    }
                }
            }
        } else {
            // When there is no diary entry from the search screen
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
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

@Composable
private fun DiaryListContent(
    modifier: Modifier = Modifier,
    diaries: List<Diary>,
    isFiltered: Boolean,
    onAddEntry: () -> Unit,
    selectedIds: Set<Long>,
    diaryFilters: DiaryFilters,
    onCancelSelection: () -> Unit,
    onToggleSelection: (id: Long?) -> Unit,
    onRemoveSelection: (id: Long?) -> Unit,
    onAddSelection: (id: Long?) -> Unit,
    onDeleteDiaries: (selectedIds: List<Long>) -> Unit,
    onApplyFilters: (filters: DiaryFilters) -> Unit,
) {
    // We want to keep showing the search bar even for an empty list
    // if filters have been applied
    val filteredEmpty = diaries.isEmpty() && isFiltered

    if (diaries.isNotEmpty() || filteredEmpty) {
        DiaryList(
            modifier = modifier,
            diaries = diaries,
            onAddEntry = onAddEntry,
            onToggleSelection = onToggleSelection,
            inSelectionMode = selectedIds.isNotEmpty(),
            selectedIds = selectedIds,
            onRemoveSelection = onRemoveSelection,
            onAddSelection = onAddSelection,
            onApplyFilters = onApplyFilters,
            diaryFilters = diaryFilters,
            onDeleteDiaries = onDeleteDiaries,
            onCancelSelection = onCancelSelection,
        )
    } else {
        EmptyDiaryList(
            onAddEntry = onAddEntry,
        )
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(bottom = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            alignment = Alignment.CenterVertically,
        ),
    ) {
        CircularProgressIndicator()

        Text(
            text = "Loading Diaries",
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun EmptyDiaryList(
    modifier: Modifier = Modifier,
    onAddEntry: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            textAlign = TextAlign.Center,
            text = "Uh Uhh, it's very lonely here 😔",
            style = MaterialTheme.typography.headlineMedium,
            fontSize = 20.sp,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Why don't you start writing something...",
            style = MaterialTheme.typography.bodySmall,
            fontSize = 14.sp,
        )

        TextButton(
            onClick = onAddEntry,
        ) {
            Text("Add Entry")
        }
    }
}

@Composable
private fun DiaryItem(
    modifier: Modifier = Modifier,
    diary: Diary,
    selected: Boolean,
    inSelectionMode: Boolean,
) {
    val transition = updateTransition(selected, label = "selected")
    val padding by transition.animateDp(label = "padding") { _ ->
        if (inSelectionMode) 4.dp else 0.dp
    }

    val roundedCornerShape by transition.animateDp(label = "corner") { _ ->
        if (selected) 16.dp else 0.dp
    }

    Box(
        modifier = Modifier
            .height(110.dp)
            .padding(padding)
            .clip(RoundedCornerShape(roundedCornerShape))
            .fillMaxWidth()
            .then(modifier),
    ) {
        Card(
            shape = RoundedCornerShape(
                topStart = 0.dp,
                bottomStart = 12.dp,
                topEnd = 12.dp,
                bottomEnd = 0.dp,
            ),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(
                                topStart = 0.dp,
                                topEnd = 12.dp,
                                bottomStart = 12.dp,
                                bottomEnd = 0.dp,
                            ),
                        )
                        .padding(horizontal = 25.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = buildAnnotatedString {
                            val date = diary.date.toLocalDateTime(TimeZone.UTC).date

                            withStyle(
                                SpanStyle(
                                    fontFamily = montserratAlternativesFontFamily(),
                                    fontSize = 14.sp,
                                ),
                            ) {
                                append(
                                    date.format("E")
                                        .uppercase(),
                                )
                            }

                            appendLine()

                            withStyle(
                                SpanStyle(
                                    fontFamily = montserratAlternativesFontFamily(),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                ),
                            ) {
                                append(date.dayOfMonth.toString())
                            }
                            appendLine()

                            withStyle(
                                SpanStyle(
                                    fontFamily = montserratAlternativesFontFamily(),
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                ),
                            ) {
                                append(
                                    date.format("MMM")
                                        .uppercase(),
                                )
                            }
                            appendLine()

                            withStyle(
                                SpanStyle(
                                    fontFamily = montserratAlternativesFontFamily(),
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                ),
                            ) {
                                append(date.year.toString())
                            }

                            toAnnotatedString()
                        },
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp,
                    )
                }

                // Diary Entry
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.Top),
                    text = diary.entry,
                    letterSpacing = (-0.3).sp,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Justify,
                )
            }
        }

        // Selection mode icon
        if (inSelectionMode) {
            val iconModifier = Modifier
                .padding(top = 12.dp, start = 4.dp)
                .size(20.dp)

            if (selected) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null,
                    modifier = iconModifier,
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.RadioButtonUnchecked,
                    tint = Color.White.copy(alpha = 0.7f),
                    contentDescription = null,
                    modifier = iconModifier,
                )
            }
        }

        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = null,
            modifier = Modifier.padding(12.dp).size(20.dp).align(Alignment.BottomEnd),
        )
    }
}

@Composable
private fun ErrorContent(modifier: Modifier) {
    Box(
        modifier = modifier.padding(bottom = 64.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Error loading diaries",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}

private fun Set<Long>.addOrRemove(id: Long?): Set<Long> {
    if (id == null) return this
    return if (this.contains(id)) this.minus(id) else this.plus(id)
}
