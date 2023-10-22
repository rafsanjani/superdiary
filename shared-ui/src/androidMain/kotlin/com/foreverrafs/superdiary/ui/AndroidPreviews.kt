package com.foreverrafs.superdiary.ui

import android.content.res.Configuration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.ui.components.ConfirmDeleteDialog
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryFilters
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryList
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryListScreen
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryListScreenState
import com.foreverrafs.superdiary.ui.feature.diarylist.components.DiaryDatePicker
import com.foreverrafs.superdiary.ui.style.SuperdiaryAppTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.toKotlinLocalDate
import java.time.LocalDate
import kotlin.random.Random

@Preview
@Composable
fun LoadingDiariesPreview() {
    SuperdiaryAppTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            DiaryListScreen(
                state = DiaryListScreenState.Loading,
                onAddEntry = {},
                onApplyFilters = {},
                onDeleteDiaries = {
                    true
                },
                onToggleFavorite = {},
                showSearchBar = false,
                diaryFilters = DiaryFilters(),
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
                onAddEntry = {},
                onApplyFilters = {},
                onDeleteDiaries = { true },
                onToggleFavorite = {},
                showSearchBar = false,
                diaryFilters = DiaryFilters(),
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
                selectedIds = setOf(),
                onAddEntry = { /*TODO*/ },
                inSelectionMode = false,
                onRemoveSelection = {},
                onAddSelection = {},
                onToggleSelection = {},
                onApplyFilters = {},
                diaryFilters = DiaryFilters(),
                onDeleteDiaries = {},
                onCancelSelection = {},
                onToggleFavorite = {},
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
                onAddEntry = {},
                onApplyFilters = {},
                onDeleteDiaries = { true },
                onToggleFavorite = {},
                showSearchBar = false,
                diaryFilters = DiaryFilters(),
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
                onAddEntry = {},
                onApplyFilters = {},
                onDeleteDiaries = { true },
                onToggleFavorite = {},
                showSearchBar = true,
                diaryFilters = DiaryFilters(),

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
                onAddEntry = {},
                onApplyFilters = {},
                showSearchBar = true,
                onDeleteDiaries = { true },
                onToggleFavorite = {},
                diaryFilters = DiaryFilters(),
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
                onAddEntry = {},
                inSelectionMode = true,
                selectedIds = setOf(0, 1),
                onRemoveSelection = {},
                onToggleSelection = {},
                onApplyFilters = {},
                onAddSelection = {},
                diaryFilters = DiaryFilters(),
                onDeleteDiaries = {},
                onCancelSelection = {},
                onToggleFavorite = {},
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
                onDismissRequest = { /*TODO*/ },
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
