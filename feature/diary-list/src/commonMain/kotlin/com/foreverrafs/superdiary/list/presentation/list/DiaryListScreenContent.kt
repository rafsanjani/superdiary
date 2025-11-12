@file:Suppress("TooManyFunctions")

package com.foreverrafs.superdiary.list.presentation.list

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.components.diarylist.DiaryFilters
import com.components.diarylist.DiaryList
import com.components.diarylist.DiaryListActions
import com.foreverrafs.superdiary.common.utils.format
import com.foreverrafs.superdiary.design.components.AppBar
import com.foreverrafs.superdiary.design.components.ConfirmDeleteDialog
import com.foreverrafs.superdiary.design.components.SuperdiaryNavigationIcon
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.utils.toDate
import com.mohamedrejeb.richeditor.annotation.ExperimentalRichTextApi
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import kotlin.math.roundToInt
import kotlin.time.Clock
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate


@OptIn(ExperimentalComposeUiApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun DiaryListScreenContent(
    screenModel: DiaryListScreenModel,
    diaryFilters: DiaryFilters,
    showSearchBar: Boolean,
    diaryListActions: DiaryListActions,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
    clock: Clock = Clock.System,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedIds by rememberSaveable {
        mutableStateOf(setOf<Long>())
    }

    @Suppress("NAME_SHADOWING")
    val diaryListActions =
        remember {
            diaryListActions.copy(
                onAddSelection = { diaryId ->
                    diaryId?.let {
                        selectedIds = selectedIds.plus(diaryId)
                    }
                },
                onRemoveSelection = { diaryId ->
                    diaryId?.let {
                        selectedIds = selectedIds.minus(diaryId)
                    }
                },
                onToggleSelection = {
                    selectedIds = selectedIds.addOrRemove(it)
                },
                onCancelSelection = {
                    selectedIds = emptySet()
                },
            )
        }

    fun onBack() {
        if (selectedIds.isNotEmpty()) {
            diaryListActions.onCancelSelection()
        } else {
            diaryListActions.onBackPressed()
        }
    }

    BackHandler {
        onBack()
    }

    Scaffold(
        topBar = {
            AppBar(
                navigationIcon = {
                    SuperdiaryNavigationIcon(onClick = ::onBack)
                },
                avatarUrl = screenModel.avatarUrl,
                onProfileClick = onProfileClick,
            )
        },
        floatingActionButton = {
            // Only show FAB when there is an entry
            if (screenModel.diaries.isEmpty()) {
                return@Scaffold
            }
            FloatingActionButton(
                modifier = Modifier.testTag("button_add_entry"),
                onClick = diaryListActions.onAddEntry,
                shape = RoundedCornerShape(4.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                )
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        modifier = modifier,
    ) {
        Box(modifier = Modifier.padding(it)) {
            if (screenModel.isLoading) {
                LoadingContent(modifier = Modifier.fillMaxSize())
                return@Box
            }

            if (screenModel.error != null) {
                ErrorContent(
                    modifier = Modifier.fillMaxSize(),
                )
                return@Box
            }

            DiaryListContent(
                modifier = Modifier.fillMaxSize(),
                diaries = screenModel.diaries,
                isFiltered = screenModel.isFiltered,
                showSearchBar = showSearchBar,
                onAddEntry = diaryListActions.onAddEntry,
                diaryListActions = diaryListActions,
                selectedIds = selectedIds,
                diaryFilters = diaryFilters,
                clock = clock,
                snackbarHostState = snackbarHostState,
            )
        }
    }
}

/**
 * Main content screen of a diary list
 *
 * @param diaries List of diaries to display in the list
 * @param isFiltered Determines whether the rendered list is a result of a
 *    filter operation
 * @param showSearchBar Determines whether the search bar should be shown.
 *    It is hidden
 */
@Composable
private fun DiaryListContent(
    diaries: List<Diary>,
    isFiltered: Boolean,
    showSearchBar: Boolean,
    diaryListActions: DiaryListActions,
    selectedIds: Set<Long>,
    diaryFilters: DiaryFilters,
    snackbarHostState: SnackbarHostState,
    onAddEntry: () -> Unit,
    modifier: Modifier = Modifier,
    clock: Clock = Clock.System,
) {
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
                coroutineScope.launch {
                    showConfirmDeleteDialog = false
                    val isSuccess =
                        diaryListActions.onDeleteDiaries(
                            diaries.filter { selectedIds.contains(it.id) },
                        )

                    val message =
                        if (isSuccess) {
                            "${selectedIds.size} item(s) deleted!"
                        } else {
                            "Error deleting diaries"
                        }

                    diaryListActions.onCancelSelection()

                    snackbarHostState.showSnackbar(
                        message,
                    )
                }
            },
        )
    }

    // We want to keep showing the search bar even for an empty list
    // if filters have been applied
    val filteredEmpty = diaries.isEmpty() && isFiltered

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        val listState = rememberLazyListState()

        if (diaries.isNotEmpty() || filteredEmpty) {
            DiaryList(
                modifier = Modifier.fillMaxSize(),
                diaries = diaries,
                inSelectionMode =
                selectedIds.isNotEmpty(),
                diaryFilters = diaryFilters,
                selectedIds = selectedIds,
                showSearchBar = showSearchBar,
                onDeleteDiaries = {
                    showConfirmDeleteDialog = true
                },
                onCancelSelection = diaryListActions.onCancelSelection,
                diaryListActions = diaryListActions,
                snackbarHostState = snackbarHostState,
                listState = listState,
                clock = clock,
            )
        } else {
            EmptyDiaryList(
                modifier = Modifier.fillMaxSize(),
                onAddEntry = onAddEntry,
            )
        }
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
    onAddEntry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 8.dp),
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

private enum class Anchors {
    Start,
    End,
}

/** This is reused in DashboardScreenContent */
@OptIn(ExperimentalRichTextApi::class)
@Composable
fun DiaryItem(
    diary: Diary,
    selected: Boolean,
    inSelectionMode: Boolean,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val transition = updateTransition(selected, label = "selected")
    val padding by transition.animateDp(label = "padding") { _ ->
        if (inSelectionMode) 4.dp else 0.dp
    }

    var draggableWidth by remember { mutableFloatStateOf(0f) }

    val state = rememberSaveable(saver = AnchoredDraggableState.Saver()) {
        AnchoredDraggableState(
            initialValue = Anchors.Start,
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .onSizeChanged {
                draggableWidth = it.width.toFloat()
                state.updateAnchors(
                    DraggableAnchors {
                        Anchors.Start at 0f
                        Anchors.End at -(draggableWidth * 0.25f)
                    },
                )
            }
            .anchoredDraggable(
                state = state,
                flingBehavior = AnchoredDraggableDefaults.flingBehavior(
                    state = state,
                    positionalThreshold = { distance: Float -> distance * 0.25f },
                ),
                orientation = Orientation.Horizontal,
                overscrollEffect = rememberOverscrollEffect(),
            )
            .height(110.dp)
            .padding(padding),
    ) {
        Card(
            shape = RoundedCornerShape(
                topStart = 0.dp,
                bottomStart = 8.dp,
                topEnd = 8.dp,
                bottomEnd = 0.dp,
            ),
            modifier = Modifier
                .zIndex(.9f)
                .fillMaxWidth()
                .offset {
                    IntOffset(
                        // Workaround for offset getting read before being initialized bug in anchoreddraggable
                        x = if (state.offset.isNaN()) {
                            0
                        } else {
                            state.offset.roundToInt()
                        },
                        y = 0,
                    )
                },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.semantics {
                    stateDescription = if (diary.isFavorite) {
                        "Favorite"
                    } else {
                        "Not favorite"
                    }

                    customActions = listOf(
                        CustomAccessibilityAction(
                            label = "Toggle Favorite",
                            action = {
                                onToggleFavorite()
                                true
                            },
                        ),
                    )
                }.fillMaxSize(),
            ) {
                DateCard(
                    modifier = Modifier.weight(2.3f),
                    date = diary.date.toDate(),
                )

                val state = rememberRichTextState()
                LaunchedEffect(Unit) {
                    state.setHtml(diary.entry)
                }

                // Diary Entry
                RichText(
                    modifier = Modifier
                        .weight(8f)
                        .clearAndSetSemantics { }
                        .padding(
                            start = 8.dp,
                            end = 8.dp,
                            bottom = 8.dp,
                            top = 16.dp,
                        )
                        .align(Alignment.Top),
                    letterSpacing = (-0.3).sp,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    state = state,
                )
            }
        }
        // Selection mode icon
        if (inSelectionMode) {
            val iconModifier = Modifier
                .zIndex(1f)
                .align(Alignment.TopEnd)
                .padding(top = 12.dp, start = 4.dp).size(20.dp)

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

        val totalSize = with(LocalDensity.current) {
            (draggableWidth * 0.25f).toDp()
        }

        val coroutineScope = rememberCoroutineScope()
        Box(
            modifier = Modifier
                .size(totalSize)
                .zIndex(.1f)
                .align(Alignment.CenterEnd),
            contentAlignment = Alignment.Center,
        ) {
            IconButton(
                onClick = {
                    onToggleFavorite()
                    coroutineScope.launch {
                        // don't reverse the animation straight away
                        delay(250)
                        state.animateTo(Anchors.Start)
                    }
                },
            ) {
                Icon(
                    imageVector = if (diary.isFavorite) {
                        Icons.Default.Favorite
                    } else {
                        Icons.Default.FavoriteBorder
                    },
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
private fun DateCard(
    date: LocalDate,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 12.dp,
                    bottomStart = 12.dp,
                    bottomEnd = 0.dp,
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            modifier = Modifier.semantics {
                contentDescription = "Entry for ${date.format("EEE dd MMMM yyyy")}"
            },
            text = buildDateAnnotatedString(date),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
private fun buildDateAnnotatedString(date: LocalDate): AnnotatedString =
    buildAnnotatedString {
        append(
            date.format("E").uppercase(),
        )

        appendLine()

        withStyle(
            SpanStyle(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
            ),
        ) {
            append(date.day.toString())
        }
        appendLine()

        withStyle(
            SpanStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
            ),
        ) {
            append(
                date.format("MMM").uppercase(),
            )
        }
        appendLine()

        withStyle(
            SpanStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
            ),
        ) {
            append(date.year.toString())
        }
    }

@Composable
private fun ErrorContent(modifier: Modifier = Modifier) {
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
