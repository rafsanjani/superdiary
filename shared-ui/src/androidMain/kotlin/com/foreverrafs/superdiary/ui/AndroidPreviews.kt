@file:Suppress("TooManyFunctions")

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
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.data.model.Streak
import com.foreverrafs.superdiary.data.utils.toDate
import com.foreverrafs.superdiary.ui.components.ConfirmDeleteDialog
import com.foreverrafs.superdiary.ui.components.SuperDiaryAppBar
import com.foreverrafs.superdiary.ui.feature.creatediary.screen.CreateDiaryScreenContent
import com.foreverrafs.superdiary.ui.feature.dashboard.DashboardScreenContent
import com.foreverrafs.superdiary.ui.feature.dashboard.DashboardViewModel
import com.foreverrafs.superdiary.ui.feature.details.DetailScreenContent
import com.foreverrafs.superdiary.ui.feature.diarychat.DiaryChatScreenContent
import com.foreverrafs.superdiary.ui.feature.diarychat.DiaryChatViewModel
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryFilters
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryListActions
import com.foreverrafs.superdiary.ui.feature.diarylist.components.DiaryDatePicker
import com.foreverrafs.superdiary.ui.feature.diarylist.screen.DiaryList
import com.foreverrafs.superdiary.ui.feature.diarylist.screen.DiaryListScreenContent
import com.foreverrafs.superdiary.ui.feature.diarylist.screen.DiaryListViewState
import com.foreverrafs.superdiary.ui.style.SuperdiaryTheme
import java.time.LocalDate
import kotlin.random.Random
import kotlinx.datetime.Clock
import kotlinx.datetime.toKotlinLocalDate

private val diaryListActions = DiaryListActions(
    onAddEntry = {},
    onDeleteDiaries = { true },
    onToggleFavorite = { true },
    onApplyFilters = {},
    onDiaryClicked = {},
)

@Composable
internal fun TestAppContainer(content: @Composable () -> Unit) {
    SuperdiaryTheme {
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

@PreviewSuperDiary
@Composable
private fun DiaryChatPreview() {
    TestAppContainer {
        DiaryChatScreenContent(
            screenState = DiaryChatViewModel.DiaryChatViewState(
                isResponding = true,
            ),
        )
    }
}

@PreviewSuperDiary
@Composable
private fun LoadingDiariesPreview() {
    SuperdiaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            DiaryListScreenContent(
                state = DiaryListViewState.Loading,
                showSearchBar = false,
                diaryFilters = DiaryFilters(),
                diaryListActions = diaryListActions,
            )
        }
    }
}

@PreviewSuperDiary
@Composable
private fun ErrorLoadingDiariesPreview() {
    SuperdiaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            DiaryListScreenContent(
                state = DiaryListViewState.Error(Error()),
                showSearchBar = false,
                diaryFilters = DiaryFilters(),
                diaryListActions = diaryListActions,
            )
        }
    }
}

@PreviewSuperDiary
@Composable
private fun EmptySearchDiaryListPreview() {
    TestAppContainer {
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

@PreviewSuperDiary
@Composable
private fun EmptyDiaryListPreview() {
    SuperdiaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            DiaryListScreenContent(
                state = DiaryListViewState.Content(listOf(), false),
                showSearchBar = false,
                diaryFilters = DiaryFilters(),
                diaryListActions = diaryListActions,
            )
        }
    }
}

@PreviewSuperDiary
@Composable
private fun DiaryListPreview() {
    SuperdiaryTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            DiaryListScreenContent(
                state = DiaryListViewState.Content(
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

@PreviewSuperDiary
@Composable
private fun CreateDiaryPreview() {
    SuperdiaryTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            CreateDiaryScreenContent(
                onNavigateBack = {},
                onGenerateAI = { _, _ -> },
                onSaveDiary = {},
                isGeneratingFromAi = false,
            )
        }
    }
}

@PreviewSuperDiary
@Composable
private fun CreateDiaryPreviewNonEditable() {
    SuperdiaryTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            CreateDiaryScreenContent(
                onNavigateBack = {},
                onGenerateAI = { _, _ -> },
                onSaveDiary = {},
                isGeneratingFromAi = false,
            )
        }
    }
}

@PreviewSuperDiary
@Composable
private fun FilteredEmptyPreview() {
    SuperdiaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            Surface(
                color = MaterialTheme.colorScheme.background,
            ) {
                DiaryListScreenContent(
                    state = DiaryListViewState.Content(
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
}

@PreviewSuperDiary
@Composable
private fun SelectedDiariesPreview() {
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
@PreviewSuperDiary
private fun DiaryDatePickerPreview() {
    TestAppContainer {
        DiaryDatePicker(
            onDismissRequest = {},
            onDateSelected = {},
            selectedDate = LocalDate.now().toKotlinLocalDate(),
        )
    }
}

@Composable
@PreviewSuperDiary
private fun DeleteDialogPreview() {
    TestAppContainer {
        ConfirmDeleteDialog(
            onDismiss = {},
            onConfirm = {},
        )
    }
}

@Composable
@PreviewSuperDiary
private fun DashboardPreview() {
    TestAppContainer {
        DashboardScreenContent(
            state = DashboardViewModel.DashboardScreenState.Content(
                (0..1).map {
                    Diary(
                        id = it.toLong(),
                        entry = "<strong>Awesome</strong> Diary",
                        date = Clock.System.now(),
                        isFavorite = false,
                    )
                },
                20,
                "",
                Streak(
                    0,
                    Clock.System.now().toDate(),
                    Clock.System.now().toDate(),
                ),
                bestStreak = Streak(
                    0,
                    Clock.System.now().toDate(),
                    Clock.System.now().toDate(),
                ),
            ),
            onAddEntry = {},
            onSeeAll = {},
            onToggleFavorite = {},
        )
    }
}

@Composable
@PreviewSuperDiary
private fun DetailPreview() {
    SuperdiaryTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            DetailScreenContent(
                onNavigateBack = {},
                onDeleteDiary = {},
                diary = Diary(
                    entry = """
                            <p style="text-align:justify;">Hello Diary, I did something awful today too.
                                I kept eating a very large bowl of rice till I couldn't take
                                it any much longer. I think this will go down in history as
                                the greatest rice eating bout of all time.
                                 Hello Diary, I did something awful today too.
                                I kept eating a very large bowl of rice till I couldn't take
                                it any much longer. I think this will go down in history as
                                the greatest rice eating bout of all time.
                                 Hello Diary, I did something awful today too.
                                I kept eating a very large bowl of rice till I couldn't take
                                it any much longer. I think this will go down in history as
                                the greatest rice eating bout of all time.
                                 Hello Diary, I did something awful today too.
                                I kept eating a very large bowl of rice till I couldn't take
                                it any much longer. I think this will go down in history as
                                the greatest rice eating bout of all time.
                                 Hello Diary, I did something awful today too.
                                I kept eating a very large bowl of rice till I couldn't take
                                it any much longer. I think this will go down in history as
                                the greatest rice eating bout of all time.
                                 Hello Diary, I did something awful today too.
                                I kept eating a very large bowl of rice till I couldn't take
                                it any much longer. I think this will go down in history as
                                the greatest rice eating bout of all time.
                                 Hello Diary, I did something awful today too.
                                I kept eating a very large bowl of rice till I couldn't take
                                it any much longer. I think this will go down in history as
                                the greatest rice eating bout of all time.
                                 Hello Diary, I did something awful today too.
                                I kept eating a very large bowl of rice till I couldn't take
                                it any much longer. I think this will go down in history as
                                the greatest rice eating bout of all time.
                            <p/>
                    """.trimIndent(),
                    id = 1000,
                    date = Clock.System.now(),
                    isFavorite = false,
                ),
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Night")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Day")
annotation class PreviewSuperDiary
