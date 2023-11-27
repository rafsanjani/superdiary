package com.foreverrafs.superdiary.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.ui.components.ConfirmDeleteDialog
import com.foreverrafs.superdiary.ui.components.SuperDiaryAppBar
import com.foreverrafs.superdiary.ui.feature.creatediary.screen.CreateDiaryScreenContent
import com.foreverrafs.superdiary.ui.feature.dashboard.DashboardScreenContent
import com.foreverrafs.superdiary.ui.feature.dashboard.DashboardScreenModel
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryFilters
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryListActions
import com.foreverrafs.superdiary.ui.feature.diarylist.components.DiaryDatePicker
import com.foreverrafs.superdiary.ui.feature.diarylist.screen.DiaryList
import com.foreverrafs.superdiary.ui.feature.diarylist.screen.DiaryListScreenContent
import com.foreverrafs.superdiary.ui.feature.diarylist.screen.DiaryListScreenState
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
    onDiaryClicked = {},
)

@Composable
internal fun TestAppContainer(content: @Composable () -> Unit) {
    SuperdiaryAppTheme {
        Scaffold(
            topBar = {
                SuperDiaryAppBar()
            },
            contentColor = MaterialTheme.colorScheme.background,
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
            ) {
                content()
            }
        }
    }
}

@SuperDiaryPreview
@Composable
fun LoadingDiariesPreview() {
    TestAppContainer {
        DiaryListScreenContent(
            state = DiaryListScreenState.Loading,
            showSearchBar = false,
            diaryFilters = DiaryFilters(),
            diaryListActions = diaryListActions,
        )
    }
}

@SuperDiaryPreview
@Composable
fun ErrorLoadingDiariesPreview() {
    SuperdiaryAppTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            DiaryListScreenContent(
                state = DiaryListScreenState.Error(Error()),
                showSearchBar = false,
                diaryFilters = DiaryFilters(),
                diaryListActions = diaryListActions,
            )
        }
    }
}

@SuperDiaryPreview
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

@SuperDiaryPreview
@Composable
fun EmptyDiaryListPreview() {
    SuperdiaryAppTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            DiaryListScreenContent(
                state = DiaryListScreenState.Content(listOf(), false),
                showSearchBar = false,
                diaryFilters = DiaryFilters(),
                diaryListActions = diaryListActions,
            )
        }
    }
}

@SuperDiaryPreview
@Composable
fun DiaryListPreview() {
    SuperdiaryAppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            DiaryListScreenContent(
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

@SuperDiaryPreview
@Composable
fun CreateDiaryPreview() {
    SuperdiaryAppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            CreateDiaryScreenContent(
                onNavigateBack = {},
                onGenerateAI = { _, _ -> },
                diary = null,
                onSaveDiary = {},
                isGeneratingFromAi = false,
                onDeleteDiary = {},
            )
        }
    }
}

@SuperDiaryPreview
@Composable
fun CreateDiaryPreviewNonEditable() {
    SuperdiaryAppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            CreateDiaryScreenContent(
                onNavigateBack = {},
                onGenerateAI = { _, _ -> },
                diary = Diary(
                    id = null,
                    entry = "Sample diary",
                    date = Clock.System.now(),
                    isFavorite = false,
                ),
                onSaveDiary = {},
                isGeneratingFromAi = false,
                onDeleteDiary = {},
            )
        }
    }
}

@SuperDiaryPreview
@Composable
fun FilteredEmptyPreview() {
    TestAppContainer {
        DiaryListScreenContent(
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

@SuperDiaryPreview
@Composable
fun SelectedDiariesPreview() {
    TestAppContainer {
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

@Composable
@SuperDiaryPreview
fun DiaryDatePickerPreview() {
    TestAppContainer {
        DiaryDatePicker(
            onDismissRequest = {},
            onDateSelected = {},
            selectedDate = LocalDate.now().toKotlinLocalDate(),
        )
    }
}

@Composable
@SuperDiaryPreview
fun DeleteDialogPreview() {
    TestAppContainer {
        ConfirmDeleteDialog(
            onDismiss = {},
            onConfirm = {},
        )
    }
}

@Composable
@SuperDiaryPreview
fun DashboardPreview() {
    TestAppContainer {
        DashboardScreenContent(
            onAddEntry = {},
            state = DashboardScreenModel.DashboardScreenState.Loading,
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Night")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Day")
annotation class SuperDiaryPreview
