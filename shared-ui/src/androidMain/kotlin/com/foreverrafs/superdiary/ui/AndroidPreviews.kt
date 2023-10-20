package com.foreverrafs.superdiary.ui

import android.content.res.Configuration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryDatePicker
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryList
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryListScreen
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryListScreenState
import com.foreverrafs.superdiary.ui.style.SuperdiaryAppTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.toKotlinLocalDate
import java.time.LocalDate
import kotlin.random.Random

@Preview
@Composable
fun EmptySearchDiaryListPreview() {
    SuperdiaryAppTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            DiaryList(
                diaries = listOf(),
                onAddEntry = { /*TODO*/ },
                onFilterDiaryQuery = {},
                selectedIds = setOf(),
                removeSelection = {},
                inSelectionMode = false,
                addSelection = {},
                toggleSelection = {},
                onDateSelected = {},
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
                state = DiaryListScreenState.Content(listOf()),
                onAddEntry = {},
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
                        )
                    },
                ),
                onAddEntry = {},
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
                    )
                },
                onAddEntry = {},
                inSelectionMode = true,
                selectedIds = setOf(0, 1),
                onFilterDiaryQuery = {},
                removeSelection = {},
                toggleSelection = {},
                addSelection = {},
                onDateSelected = {},
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Night")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Day")
fun DiaryDatePickerPreview() {
    SuperdiaryAppTheme() {
        Surface(color = MaterialTheme.colorScheme.background) {
            DiaryDatePicker(
                onDismissRequest = { /*TODO*/ },
                onDateSelected = {},
                selectedDate = LocalDate.now().toKotlinLocalDate(),
            )
        }
    }
}
