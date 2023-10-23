package com.foreverrafs.superdiary.ui

import android.content.res.Configuration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.ui.components.ConfirmDeleteDialog
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryFilters
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryList
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryListActions
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryListScreen
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryListScreenState
import com.foreverrafs.superdiary.ui.feature.diarylist.components.DiaryDatePicker
import com.foreverrafs.superdiary.ui.style.SuperdiaryAppTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.toKotlinLocalDate
import java.time.LocalDate
import kotlin.random.Random

private val diaryListActions = DiaryListActions(
    onAddEntry = {},
    onDeleteDiaries = { true },
    onToggleFavorite = { true },
    onApplyFilters = {},
)

@Preview
@Composable
fun LoadingDiariesPreview() {
    SuperdiaryAppTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            DiaryListScreen(
                state = DiaryListScreenState.Loading,
                showSearchBar = false,
                diaryFilters = DiaryFilters(),
                diaryListActions = diaryListActions,
            )
        }
    }
}

@Preview
@Composable
fun ErrorLoadingDiariesPreview() {
    SuperdiaryAppTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            DiaryListScreen(
                state = DiaryListScreenState.Error(Error()),
                showSearchBar = false,
                diaryFilters = DiaryFilters(),
                diaryListActions = diaryListActions,
            )
        }
    }
}

@Preview
@Composable
fun EmptySearchDiaryListPreview() {
    SuperdiaryAppTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            DiaryList(
                diaries = listOf(),
                inSelectionMode = false,
                diaryFilters = DiaryFilters(),
                selectedIds = setOf(),
                onDeleteDiaries = {},
                onCancelSelection = {},
                diaryListActions = diaryListActions,
                snackbarHostState = SnackbarHostState(),
            )
        }
    }
}

@Preview
@Composable
fun EmptyDiaryListPreview() {
    SuperdiaryAppTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            DiaryListScreen(
                state = DiaryListScreenState.Content(listOf(), false),
                showSearchBar = false,
                diaryFilters = DiaryFilters(),
                diaryListActions = diaryListActions,
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Night")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Day")
@Composable
fun DiaryListPreview() {
    SuperdiaryAppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            DiaryListScreen(
                state = DiaryListScreenState.Content(
                    diaries = (0..10).map {
                        Diary(
                            id = Random.nextLong(),
                            entry = "Hello World $it",
                            date = Clock.System.now(),
                            isFavorite = false,
                        )
                    },
                    filtered = false,
                ),
                showSearchBar = true,
                diaryFilters = DiaryFilters(),
                diaryListActions = diaryListActions,
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Night")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Day")
@Composable
fun FilteredEmptyPreview() {
    SuperdiaryAppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            DiaryListScreen(
                state = DiaryListScreenState.Content(
                    diaries = listOf(),
                    filtered = true,
                ),
                showSearchBar = true,
                diaryFilters = DiaryFilters(),
                diaryListActions = diaryListActions,
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Night")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Day")
@Composable
fun SelectedDiariesPreview() {
    SuperdiaryAppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            DiaryList(
                diaries = (0..10).map {
                    Diary(
                        id = Random.nextLong(),
                        entry = "Hello World $it",
                        date = Clock.System.now(),
                        isFavorite = false,
                    )
                },
                inSelectionMode = true,
                diaryFilters = DiaryFilters(),
                selectedIds = setOf(0, 1),
                onDeleteDiaries = {},
                onCancelSelection = {},
                diaryListActions = diaryListActions,
                snackbarHostState = SnackbarHostState(),
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Night")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Day")
fun DiaryDatePickerPreview() {
    SuperdiaryAppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            DiaryDatePicker(
                onDismissRequest = {},
                onDateSelected = {},
                selectedDate = LocalDate.now().toKotlinLocalDate(),
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Night")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Day")
fun DeleteDialogPreview() {
    SuperdiaryAppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ConfirmDeleteDialog(
                onDismiss = {},
                onConfirm = {},
            )
        }
    }
}
