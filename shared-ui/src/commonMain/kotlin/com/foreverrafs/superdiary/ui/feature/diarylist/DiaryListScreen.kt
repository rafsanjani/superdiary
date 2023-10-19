package com.foreverrafs.superdiary.ui.feature.diarylist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.utils.groupByDate
import com.foreverrafs.superdiary.ui.components.SuperDiaryAppBar
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
                if (state.diaries.isNotEmpty()) {
                    var query by remember {
                        mutableStateOf("")
                    }

                    val filteredDiaries by remember(query) {
                        mutableStateOf(
                            state.diaries.filter { it.entry.contains(query, false) },
                        )
                    }

                    DiaryList(
                        modifier = Modifier.fillMaxSize(),
                        diaries = filteredDiaries,
                        onAddEntry = onAddEntry,
                        onFilterDiaryQuery = {
                            query = it
                        },
                    )
                } else {
                    EmptyDiaryList(
                        onAddEntry = onAddEntry,
                    )
                }
            }

            is DiaryListScreenState.Error -> Error(modifier = Modifier.fillMaxWidth())

            is DiaryListScreenState.Loading -> Loading(modifier = Modifier.wrapContentSize())
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiaryList(
    modifier: Modifier = Modifier,
    diaries: List<Diary>,
    onAddEntry: () -> Unit,
    onFilterDiaryQuery: (query: String) -> Unit,
) {
    var selectedIds by rememberSaveable {
        mutableStateOf(emptySet<Long>())
    }

    val inSelectionMode by remember {
        derivedStateOf { selectedIds.isNotEmpty() }
    }

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
        AnimatedVisibility(
            visible = inSelectionMode,
            modifier = Modifier
                .zIndex(1f)
                .fillMaxWidth()
                .wrapContentHeight(),
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(color = Color.Green),
            ) {
                Text("Selected Items: ${selectedIds.size}")
            }
        }

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
                                        selectedIds = selectedIds.plus(it)
                                    }
                                }
                            },
                            deSelectGroup = {
                                diaries.forEach { diary ->
                                    diary.id?.let {
                                        selectedIds = selectedIds.minus(it)
                                    }
                                }
                            },
                        )
                    }

                    items(
                        items = diaries,
                        key = { item -> item.id.toString() },
                    ) { diary ->
                        val isSelected by remember { derivedStateOf { diary.id in selectedIds } }

                        DiaryItem(
                            modifier = Modifier
                                .animateItemPlacement()
                                .combinedClickable(
                                    onClick = {
                                        if (inSelectionMode) {
                                            selectedIds = selectedIds.addOrRemove(diary.id)
                                        } else {
                                            // Process regular click here
                                        }
                                    },
                                    onLongClick = {
                                        selectedIds = selectedIds.addOrRemove(diary.id)
                                    },
                                ),
                            diary = diary,
                            selected = isSelected,
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

private fun Set<Long>.addOrRemove(id: Long?): Set<Long> {
    if (id == null) return this
    return if (this.contains(id)) this.minus(id) else this.plus(id)
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

    val roundedCornerShape by transition.animateDp(label = "corner") { selected ->
        if (selected) 16.dp else 0.dp
    }
    Box(
        modifier = modifier
            .height(120.dp)
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
            if (selected) {
                Icon(
                    Icons.Filled.CheckCircle,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(4.dp)
                        .border(2.dp, MaterialTheme.colorScheme.background, CircleShape)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.background),
                )
            } else {
                Icon(
                    Icons.Filled.RadioButtonUnchecked,
                    tint = Color.White.copy(alpha = 0.7f),
                    contentDescription = null,
                    modifier = Modifier.padding(6.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SearchBar(
    modifier: Modifier = Modifier,
    onQueryChanged: (query: String) -> Unit,
    onFilterClicked: () -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }

    val cornerRadius by animateDpAsState(
        if (isFocused) 8.dp else 4.dp,
    )

    val border by animateDpAsState(
        if (isFocused) 2.dp else 1.dp,
    )

    LaunchedEffect(query) {
        onQueryChanged(query)
    }

    TextField(
        modifier = modifier
            .onFocusChanged {
                isFocused = it.hasFocus
            }
            .border(
                width = border,
                color = Color.Black,
                shape = RoundedCornerShape(cornerRadius),
            ),
        singleLine = true,
        value = query,
        onValueChange = { query = it },
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
        ),
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
        shape = RoundedCornerShape(cornerRadius),
        placeholder = {
            Text(
                modifier = Modifier.alpha(0.5f),
                text = "Search in diaries",
                style = MaterialTheme.typography.titleMedium,
            )
        },
        textStyle = MaterialTheme.typography.titleMedium,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search,
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                keyboardController?.hide()
                focusManager.clearFocus(true)
            },
        ),
    )
}

@Composable
private fun Error(modifier: Modifier) {
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

@Composable
private fun DiaryHeader(
    modifier: Modifier = Modifier,
    text: String,
    inSelectionMode: Boolean,
    selectGroup: () -> Unit,
    deSelectGroup: () -> Unit,
    selected: Boolean,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = text,
                style = MaterialTheme.typography.headlineMedium,
            )

            if (inSelectionMode) {
                if (selected) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(4.dp)
                            .border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                            .clip(CircleShape)
                            .clickable {
                                deSelectGroup()
                            },
                    )
                } else {
                    Icon(
                        Icons.Filled.RadioButtonUnchecked,
                        tint = Color.Black.copy(alpha = 0.55f),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable {
                                selectGroup()
                            },
                    )
                }
            }
        }
    }
}
