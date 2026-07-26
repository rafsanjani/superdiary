@file:Suppress("TooManyFunctions")

package com.foreverrafs.superdiary.list.presentation.list

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.components.diarylist.DiaryFilters
import com.components.diarylist.DiaryList
import com.components.diarylist.DiaryListActions
import com.foreverrafs.superdiary.design.components.AppBar
import com.foreverrafs.superdiary.design.components.ConfirmDeleteDialog
import com.foreverrafs.superdiary.design.style.CREATE_DIARY_SHARED_BOUNDS_KEY
import com.foreverrafs.superdiary.design.style.LocalRootAnimatedContentScope
import com.foreverrafs.superdiary.design.style.LocalSharedTransitionScope
import com.foreverrafs.superdiary.domain.model.Diary
import kotlin.time.Clock
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun DiaryListScreenContent(
    screenModel: DiaryListScreenModel,
    diaryFilters: DiaryFilters,
    showSearchBar: Boolean,
    diaryListActions: DiaryListActions,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    avatarUrl: String? = null,
    onProfileClick: () -> Unit = {},
    clock: Clock = Clock.System,
    listState: LazyListState = rememberLazyListState(),
) {
    var selectedIds by rememberSaveable {
        mutableStateOf(setOf<Long>())
    }
    val diaries = screenModel.diaries.collectAsLazyPagingItems()

    @Suppress("NAME_SHADOWING")
    val diaryListActions = remember {
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

    NavigationBackHandler(
        isBackEnabled = true,
        state = rememberNavigationEventState(
            currentInfo = NavigationEventInfo.None,
        ),
        onBackCompleted = ::onBack,
    )

    Scaffold(
        floatingActionButton = {
            // Only show FAB when there is an entry
            if (diaries.itemCount == 0) {
                return@Scaffold
            }

            val sharedTransitionScope = LocalSharedTransitionScope.current
            val sharedAnimatedContentScope = LocalRootAnimatedContentScope.current
                ?: LocalNavAnimatedContentScope.current

            with(sharedTransitionScope) {
                FloatingActionButton(
                    modifier = Modifier
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(
                                key = CREATE_DIARY_SHARED_BOUNDS_KEY,
                            ),
                            animatedVisibilityScope = sharedAnimatedContentScope,
                        )
                        .testTag("button_add_entry"),
                    onClick = diaryListActions.onAddEntry,
                    shape = RoundedCornerShape(4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                    )
                }
            }
        },
        topBar = {
            AppBar(
                avatarUrl = avatarUrl,
                onProfileClick = onProfileClick,
                title = "Reflections",
            )
        },
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier.padding(it).padding(8.dp),
        ) {
            val hasLoadedItems = diaries.itemCount > 0
            val isInitialLoad = !hasLoadedItems &&
                (screenModel.isLoading || diaries.loadState.refresh is LoadState.Loading)

            if (isInitialLoad) {
                LoadingContent(modifier = Modifier.fillMaxSize())
                return@Box
            }

            val pagingError = diaries.loadState.refresh as? LoadState.Error
            if (!hasLoadedItems && (screenModel.error != null || pagingError != null)) {
                ErrorContent(
                    modifier = Modifier.fillMaxSize(),
                )
                return@Box
            }

            DiaryListContent(
                diaries = diaries,
                isFiltered = screenModel.isFiltered,
                showSearchBar = showSearchBar,
                diaryListActions = diaryListActions,
                selectedIds = selectedIds,
                diaryFilters = diaryFilters,
                snackbarHostState = snackbarHostState,
                onAddEntry = diaryListActions.onAddEntry,
                modifier = Modifier.fillMaxSize(),
                clock = clock,
                listState = listState,
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
    diaries: LazyPagingItems<Diary>,
    isFiltered: Boolean,
    showSearchBar: Boolean,
    diaryListActions: DiaryListActions,
    selectedIds: Set<Long>,
    diaryFilters: DiaryFilters,
    snackbarHostState: SnackbarHostState,
    onAddEntry: () -> Unit,
    listState: LazyListState,
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
                    val isSuccess = diaryListActions.onDeleteDiaries(
                        diaries.itemSnapshotList.items.filter { selectedIds.contains(it.id) },
                    )

                    val message = if (isSuccess) {
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
    val filteredEmpty = diaries.itemCount == 0 && isFiltered

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        if (diaries.itemCount > 0 || filteredEmpty) {
            DiaryList(
                modifier = Modifier.fillMaxSize(),
                diaries = diaries,
                inSelectionMode = selectedIds.isNotEmpty(),
                diaryFilters = diaryFilters,
                selectedIds = selectedIds,
                showSearchBar = showSearchBar,
                onDeleteDiaries = {
                    showConfirmDeleteDialog = true
                },
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
            modifier = Modifier.createDiarySharedBounds(),
            onClick = onAddEntry,
        ) {
            Text("Add Entry")
        }
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

@Composable
private fun Modifier.createDiarySharedBounds(): Modifier {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val sharedAnimatedContentScope = LocalRootAnimatedContentScope.current
        ?: LocalNavAnimatedContentScope.current

    return with(sharedTransitionScope) {
        sharedBounds(
            sharedContentState = rememberSharedContentState(
                key = CREATE_DIARY_SHARED_BOUNDS_KEY,
            ),
            animatedVisibilityScope = sharedAnimatedContentScope,
        )
    }
}
