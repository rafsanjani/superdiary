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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.foreverrafs.superdiary.ui.components.SuperDiaryAppBar
import com.foreverrafs.superdiary.ui.feature.diarylist.components.DiaryHeader
import com.foreverrafs.superdiary.ui.feature.diarylist.components.SearchBar
import com.foreverrafs.superdiary.ui.feature.diarylist.components.SelectionModifierBar
import com.foreverrafs.superdiary.ui.format
import com.foreverrafs.superdiary.ui.style.montserratAlternativesFontFamily
import kotlinx.datetime.LocalDateTime

@Composable
fun DiaryListScreen(
    state: DiaryListScreenState,
    modifier: Modifier = Modifier,
    onAddEntry: () -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        SuperDiaryAppBar()

        when (state) {
            is DiaryListScreenState.Content -> {
                DiaryListContent(
                    diaries = state.diaries,
                    onAddEntry = onAddEntry,
                )
            }

            is DiaryListScreenState.Error -> ErrorContent(modifier = Modifier.fillMaxWidth())

            is DiaryListScreenState.Loading -> LoadingContent(modifier = Modifier.wrapContentSize())
        }
    }
}

/**
 * Display a list of diaries from the database. We have different functions for adding
 * and removing instead of just using the toggl. There are instances where we
 * just want to add entries whether they exist or not and other times where we want to remove
 * entries at all costs.
 *
 * @param diaries The list of diaries to display
 * @param onAddEntry Add a new entry to the list
 * @param inSelectionMode Whether we are actively selecting items or not
 * @param addSelection Add an entry to the list of selected items
 * @param removeSelection Remove an entry from the list of selected items
 * @param toggleSelection Add an entry to the list of selected items or remove
 * it otherwise.
 * @param selectedIds The list of ids of the selected diary entries
 * @param onFilterDiaryQuery Called whenever the value of the search field is updated
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiaryList(
    modifier: Modifier = Modifier,
    diaries: List<Diary>,
    onAddEntry: () -> Unit,
    inSelectionMode: Boolean,
    addSelection: (id: Long?) -> Unit,
    removeSelection: (id: Long?) -> Unit,
    toggleSelection: (id: Long?) -> Unit,
    selectedIds: Set<Long>,
    onFilterDiaryQuery: (query: String) -> Unit,
) {
    val groupedDiaries = remember(diaries) {
        diaries.groupByDate()
    }

    Box(
        modifier = modifier
            .padding(8.dp),
    ) {
        var showFilterDiariesBottomSheet by remember {
            mutableStateOf(false)
        }

        // Searchbar
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            onQueryChanged = onFilterDiaryQuery,
            onFilterClicked = {
                showFilterDiariesBottomSheet = true
            },
        )

        // Modifier bar. Shown when in selection mode
        SelectionModifierBar(
            inSelectionMode = inSelectionMode,
            selectedIds = selectedIds,
        )

        if (showFilterDiariesBottomSheet) {
            FilterDiariesSheet(
                onDismissRequest = {
                    showFilterDiariesBottomSheet = false
                },
            )
        }

        if (diaries.isNotEmpty()) {
            // Offset this by the height of the Searchbar (when it isn't active)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 60.dp),
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
                                        addSelection(it)
                                    }
                                }
                            },
                            deSelectGroup = {
                                diaries.forEach { diary ->
                                    diary.id?.let {
                                        removeSelection(it)
                                    }
                                }
                            },
                        )
                    }

                    items(
                        items = diaries,
                        key = { item -> item.id.toString() },
                    ) { diary ->
                        DiaryItem(
                            modifier = Modifier
                                .animateItemPlacement()
                                .combinedClickable(
                                    onClick = {
                                        if (inSelectionMode) {
                                            toggleSelection(diary.id)
                                        } else {
                                            // Process regular click here
                                        }
                                    },
                                    onLongClick = {
                                        toggleSelection(diary.id)
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
    diaries: List<Diary>,
    onAddEntry: () -> Unit,
) {
    if (diaries.isNotEmpty()) {
        var query by remember {
            mutableStateOf("")
        }

        val filteredDiaries by remember(query) {
            mutableStateOf(
                diaries.filter { it.entry.contains(query, false) },
            )
        }

        var selectedIds by rememberSaveable {
            mutableStateOf(emptySet<Long>())
        }

        val inSelectionMode by remember {
            derivedStateOf { selectedIds.isNotEmpty() }
        }

        DiaryList(
            modifier = Modifier.fillMaxSize(),
            diaries = filteredDiaries,
            onAddEntry = onAddEntry,
            onFilterDiaryQuery = {
                query = it
            },
            toggleSelection = {
                selectedIds = selectedIds.addOrRemove(it)
            },
            inSelectionMode = inSelectionMode,
            selectedIds = selectedIds,
            removeSelection = { diaryId ->
                diaryId?.let {
                    selectedIds = selectedIds.minus(diaryId)
                }
            },
            addSelection = { diaryId ->
                diaryId?.let {
                    selectedIds = selectedIds.plus(diaryId)
                }
            },
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
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
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
        modifier = modifier
            .height(100.dp)
            .padding(padding)
            .clip(RoundedCornerShape(roundedCornerShape))
            .fillMaxWidth(),
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
                            val date = LocalDateTime.parse(diary.date).date

                            withStyle(
                                SpanStyle(
                                    fontFamily = montserratAlternativesFontFamily(),
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
                                    fontSize = 20.sp,
                                ),
                            ) {
                                append(date.dayOfMonth.toString())
                            }
                            appendLine()

                            withStyle(
                                SpanStyle(
                                    fontFamily = montserratAlternativesFontFamily(),
                                    fontWeight = FontWeight.Normal,
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

        if (inSelectionMode) {
            val iconModifier = Modifier
                .padding(top = 8.dp, start = 4.dp)
                .size(20.dp)

            if (selected) {
                Icon(
                    Icons.Filled.CheckCircle,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null,
                    modifier = iconModifier,
                )
            } else {
                Icon(
                    Icons.Filled.RadioButtonUnchecked,
                    tint = Color.White.copy(alpha = 0.7f),
                    contentDescription = null,
                    modifier = iconModifier,
                )
            }
        }
    }
}

@Composable
private fun ErrorContent(modifier: Modifier) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Error loading diaries",
            textAlign = TextAlign.Center,
            modifier = modifier,
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}

private fun Set<Long>.addOrRemove(id: Long?): Set<Long> {
    if (id == null) return this
    return if (this.contains(id)) this.minus(id) else this.plus(id)
}
