package com.foreverrafs.superdiary.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.foreverrafs.superdiary.ui.format
import com.foreverrafs.superdiary.ui.montserratAlternativesFontFamily
import com.foreverrafs.superdiary.ui.screens.DiaryScreenState
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

@Composable
fun DiaryListScreen(
    state: DiaryScreenState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        when (state) {
            is DiaryScreenState.Content -> {
                if (state.diaries.isNotEmpty()) {
                    DiaryList(
                        modifier = Modifier.fillMaxSize(),
                        diaries = state.diaries,
                    )
                } else {
                    EmptyDiaryList()
                }
            }

            is DiaryScreenState.Error -> Error(modifier = Modifier.fillMaxSize())

            is DiaryScreenState.Loading -> Loading(modifier = Modifier.wrapContentSize())
        }
    }
}

@Composable
private fun Loading(modifier: Modifier = Modifier) {
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
private fun EmptyDiaryList(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            textAlign = TextAlign.Center,
            text = "Uh Uhh, you don't have any entry",
            style = MaterialTheme.typography.headlineMedium,
            fontSize = 20.sp,
        )
        Text(
            text = "Why don't you start writing something...",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiaryList(modifier: Modifier = Modifier, diaries: List<Diary>) {
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

        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            onQueryChanged = {},
            onFilterClicked = {
                showFilterDiariesBottomSheet = true
            },
        )

        if (showFilterDiariesBottomSheet) {
            FilterDiariesSheet(
                onDismissRequest = {
                    showFilterDiariesBottomSheet = false
                },
            )
        }
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
                    DiaryHeader(
                        text = date.label,
                    )
                }

                items(
                    items = diaries,
                    key = { item -> item.id.toString() },
                ) { diary ->
                    DiaryItem(
                        diary = diary,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    modifier: Modifier = Modifier,
    onQueryChanged: (query: String) -> Unit,
    onFilterClicked: () -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(query) {
        onQueryChanged(query)
    }

    DockedSearchBar(
        modifier = modifier,
        query = query,
        onQueryChange = { query = it },
        onSearch = { active = false },
        active = active,
        onActiveChange = { active = it },
        placeholder = {
            Text(
                text = "Search diary...",
                style = MaterialTheme.typography.labelLarge,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
            )
        },
        trailingIcon = {
            Icon(
                modifier = Modifier
                    .clickable { onFilterClicked() }
                    .padding(8.dp),
                imageVector = Icons.Default.Sort,
                contentDescription = null,
            )
        },
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        ),
        shape = RoundedCornerShape(8.dp),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(4) { idx ->
                val resultText = "Suggestion $idx"

                ListItem(
                    headlineContent = { Text(resultText) },
                    supportingContent = { Text("Additional info") },
                    leadingContent = {
                        Icon(
                            Icons.Filled.LibraryBooks,
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier.clickable {
                        query = resultText
                        active = false
                    },
                )
            }
        }
    }
}

@Composable
private fun Error(modifier: Modifier) {
    Text(text = "Error loading diaries", modifier = modifier)
}

@Composable
private fun DiaryHeader(modifier: Modifier = Modifier, text: String) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
    ) {
        Text(
            modifier = Modifier.padding(vertical = 8.dp),
            text = text,
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}

@Composable
private fun DiaryItem(
    modifier: Modifier = Modifier,
    diary: Diary,
) {
    val defaultTextLines = 4

    // Used to determine whether to expand/collapse the card onClick
    var isExpanded by remember {
        mutableStateOf(false)
    }

    var maxLines by remember {
        mutableStateOf(defaultTextLines)
    }

    Card(
        modifier = modifier
            .height(120.dp)
            .animateContentSize(animationSpec = tween(easing = LinearEasing))
            .fillMaxWidth()
            .clickable {
                maxLines = if (!isExpanded) Int.MAX_VALUE else defaultTextLines
                isExpanded = !isExpanded
            },
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
                        val date = LocalDate.parse(diary.date)

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
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDiariesSheet(
    onDismissRequest: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false,
        ),
        windowInsets = WindowInsets(0),
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        var selectedDate by remember {
            mutableStateOf<LocalDate?>(
                null,
            )
        }

        var sortByWords by remember {
            mutableStateOf(false)
        }

        var sortByDate by remember {
            mutableStateOf(false)
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Sort and Filter",
                style = MaterialTheme.typography.headlineMedium,
            )
            Divider()

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Sort",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                DiaryFilterChip(
                    label = "Date",
                    onSelectionChange = { selected ->
                        sortByDate = selected

                        if (selected) {
                            sortByWords = false
                        }
                    },
                    selected = sortByDate,
                )

                DiaryFilterChip(
                    label = "Words",
                    onSelectionChange = { selected ->
                        sortByWords = selected

                        if (selected) {
                            sortByDate = false
                        }
                    },
                    selected = sortByWords,
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Filter",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
            )

            var showDatePickerDialog by remember {
                mutableStateOf(false)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (showDatePickerDialog) {
                    DiaryDatePicker(
                        onDismissRequest = { showDatePickerDialog = false },
                        onDateSelected = {
                            selectedDate = it
                        },
                        selectedDate = selectedDate,
                    )
                }

                TextButton(
                    onClick = {
                        showDatePickerDialog = true
                    },
                ) {
                    Text("Date: ${selectedDate ?: "Select Date"}")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                val count = listOf(
                    sortByWords,
                    sortByDate,
                ).count { it }
                    .plus(if (selectedDate != null) 1 else 0)

                OutlinedButton(
                    onClick = {
                        selectedDate = null
                        sortByDate = false
                        sortByWords = false
                    },
                    enabled = count != 0,
                ) {
                    if (count != 0) {
                        Badge {
                            Text(count.toString())
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text("Reset All")
                }

                Button(onClick = {}) {
                    Text("Apply")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DiaryDatePicker(
    onDismissRequest: () -> Unit,
    onDateSelected: (date: LocalDate) -> Unit,
    selectedDate: LocalDate?,
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
            ?.atStartOfDayIn(TimeZone.UTC)
            ?.toEpochMilliseconds(),
    )

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { dateMillis ->
                        val instant = Instant.fromEpochMilliseconds(dateMillis)
                        val date = instant.toLocalDateTime(TimeZone.UTC).date

                        onDateSelected(date)
                        onDismissRequest()
                    }
                },
                enabled = true,
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
            ) {
                Text(
                    text = "Cancel",
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
    ) {
        DatePicker(
            state = datePickerState,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DiaryFilterChip(
    label: String,
    onSelectionChange: (selected: Boolean) -> Unit,
    selected: Boolean,
) {
    FilterChip(
        selected = selected,
        onClick = { onSelectionChange(!selected) },
        label = { Text(label) },
        leadingIcon = {
            if (selected) {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = label,
                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                )
            }
        },
    )
}
