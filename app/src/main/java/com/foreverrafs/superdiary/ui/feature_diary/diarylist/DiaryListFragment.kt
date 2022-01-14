package com.foreverrafs.superdiary.ui.feature_diary.diarylist

import SuperDiaryTheme
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import brand
import com.foreverrafs.datepicker.DatePickerTimeline
import com.foreverrafs.domain.feature_diary.model.Diary
import com.foreverrafs.superdiary.ui.navigation.BottomNavigation
import com.foreverrafs.superdiary.ui.style.brandColorDark
import com.foreverrafs.superdiary.ui.style.diaryCardColor
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val TAG = "DiaryListFragment"

@ExperimentalComposeUiApi
@Composable
fun DiaryListScreen(
    navController: NavHostController,
    showSnackBar: Boolean,
    snackBarMessage: String,
) {
    val diaryViewModel: DiaryListViewModel = hiltViewModel()

    val viewState by diaryViewModel.viewState.collectAsState()

    viewState?.let { state ->
        DiaryListScreen(
            viewState = state,
            onDiaryDeleted = { diary: Diary ->
                diaryViewModel.onEvent(DiaryListEvent.DeleteDiary(diary))
            },
            onDateSelected = {
                diaryViewModel.getDiariesForDate(it)
            },
            diaryEventDates = diaryViewModel.diaryEventDates,
            navController = navController,
            snackBarMessage = snackBarMessage,
            showSnackBar = showSnackBar
        )
    }
}

@ExperimentalComposeUiApi
@Composable
fun DiaryListScreen(
    viewState: DiaryListState,
    onDiaryDeleted: (diary: Diary) -> Unit,
    onDateSelected: (date: LocalDate) -> Unit,
    diaryEventDates: List<LocalDate>,
    navController: NavHostController,
    snackBarMessage: String = "",
    showSnackBar: Boolean
) {
    val formatter = remember {
        DateTimeFormatter.ofPattern("dd MMMM")
    }
    val scaffoldState = rememberScaffoldState()

    SuperDiaryTheme {
        Scaffold(
            scaffoldState = scaffoldState,
            bottomBar = {
                BottomNavigation(navController = navController)
            },
        )
        {
            Surface(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                LaunchedEffect(Unit) {
                    if (showSnackBar) {
                        scaffoldState.snackbarHostState.showSnackbar(message = snackBarMessage)
                    }
                }

                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colors.brand)
                        .fillMaxSize()
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // TODO: 02/01/2022 Replace with Header composable
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "SuperDiary", modifier = Modifier
                                    .padding(8.dp),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.h4
                            )

                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .align(Alignment.CenterEnd)

                            )
                        }

                        DatePickerTimeline(
                            onDateSelected = onDateSelected,
                            backgroundColor = brandColorDark,
                            dateTextColor = Color.White,
                            selectedTextColor = Color.LightGray,
                            eventDates = diaryEventDates,
                            eventIndicatorColor = Color.White,
                            todayLabel = {
                                Text(
                                    modifier = Modifier.padding(10.dp),
                                    text = "Today: ${formatter.format(LocalDate.now())}",
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            selectedBackgroundColor = diaryCardColor
                        )

                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(vertical = 4.dp),
                            text = "Recent Entries",
                            style = MaterialTheme.typography.h6
                        )


                        when (viewState) {
                            is DiaryListState.Error -> {}
                            is DiaryListState.Loaded -> {
                                DiaryList(
                                    modifier = Modifier
                                        .weight(1f),
                                    diaries = viewState.list,
                                    onDiaryClicked = {

                                    }
                                )
                            }
                            DiaryListState.Loading -> {}
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DiaryList(
    modifier: Modifier = Modifier,
    diaries: List<Diary>,
    onDiaryClicked: (Diary) -> Unit
) {
    AnimatedVisibility(
        visible = diaries.isNotEmpty(),
        modifier = modifier.fillMaxSize(),
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(diaries) { diary ->
                DiaryCard(
                    diary = diary,
                    onDiaryClicked = onDiaryClicked
                )
            }
        }
    }

    AnimatedVisibility(
        visible = diaries.isEmpty(),
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No Diary Entry",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h4
            )
        }
    }
}

@Composable
fun DiaryCard(
    modifier: Modifier = Modifier,
    onDiaryClicked: (Diary) -> Unit,
    diary: Diary
) {
    val formatter = remember {
        DateTimeFormatter.ofPattern("dd/MM/yyyy")
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 40.dp)
            .height(72.dp)
            .clickable {
                onDiaryClicked(diary)
            },
        backgroundColor = diaryCardColor
    ) {
        Column(Modifier.padding(8.dp)) {
            Text(
                modifier = Modifier.alpha(0.50f),
                text = formatter.format(diary.date),
                fontWeight = FontWeight.Bold,
            )
            Text(text = diary.title)
        }
    }
}

@ExperimentalComposeUiApi
@Composable
@Preview
fun Preview() {
    DiaryListScreen(
        viewState = DiaryListState.Loaded(
            list = listOf(
                Diary(
                    message = "Hello my amazing people",
                    title = "Diary Entry #1"
                ),
                Diary(
                    message = "Hello my family members",
                    title = "Diary Entry #2"
                ),
                Diary(
                    message = "A nice entry",
                    title = "Diary Entry #3"
                )
            )
        ),
        onDiaryDeleted = {},
        onDateSelected = {},
        diaryEventDates = listOf(),
        navController = rememberNavController(),
        showSnackBar = false,
    )
}

