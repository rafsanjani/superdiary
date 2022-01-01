package com.foreverrafs.superdiary.framework.presentation.diarylist

import SuperDiaryTheme
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.foreverrafs.datepicker.DatePickerTimeline
import com.foreverrafs.superdiary.business.model.Diary
import com.foreverrafs.superdiary.framework.presentation.style.colorPrimary
import com.foreverrafs.superdiary.framework.presentation.style.diaryCardColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

private const val TAG = "DiaryListFragment"

@ExperimentalComposeUiApi
@AndroidEntryPoint
class DiaryListFragment : Fragment() {
    private val diaryListViewModel: DiaryListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val viewState by diaryListViewModel.viewState.collectAsState()
                val coroutineScope = rememberCoroutineScope()
                val scaffoldState = rememberScaffoldState()

                viewState?.let { it ->
                    DiaryListScreen(
                        viewState = it,
                        onDiaryDeleted = { diary ->
                            diaryListViewModel.onEvent(DiaryListEvent.DeleteDiary(diary))

                            coroutineScope.launch {
                                val action = scaffoldState.snackbarHostState.showSnackbar(
                                    message = "Note Deleted",
                                    actionLabel = "Undo"
                                )

                                if (action == SnackbarResult.ActionPerformed) {
                                    // Undo the currently deleted diary
                                }
                            }
                        },
                        onDateSelected = {
                            diaryListViewModel.getDiariesForDate(it)
                        },
                        scaffoldState = scaffoldState,
                        diaryEventDates = diaryListViewModel.diaryEventDates
                    )
                }
            }
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun DiaryListScreen(
    viewState: DiaryListState,
    onDiaryDeleted: (diary: Diary) -> Unit,
    onDateSelected: (date: LocalDate) -> Unit,
    diaryEventDates: List<LocalDate>,
    scaffoldState: ScaffoldState
) {
    val formatter = remember {
        DateTimeFormatter.ofPattern("dd MMMM")
    }

    SuperDiaryTheme {
        Scaffold(
            scaffoldState = scaffoldState,
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize(),
                color = MaterialTheme.colors.primary
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.fillMaxSize()) {
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
                            backgroundColor = colorPrimary,
                            dateTextColor = Color.White,
                            selectedTextColor = Color.LightGray,
                            eventDates = diaryEventDates,
                            eventIndicatorColor = Color.White,
                            todayLabel = {

                                Text(
                                    modifier = Modifier.padding(10.dp),
                                    text = "Today: ${formatter.format(LocalDate.now())}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
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
            Text(text = diary.message)
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
                Diary(id = Random.nextLong(), message = "Hello my amazing people", title = ""),
                Diary(id = Random.nextLong(), message = "Hello my family members", title = ""),
                Diary(id = Random.nextLong(), message = "A nice entry", title = "")
            )
        ),
        onDiaryDeleted = {},
        onDateSelected = {},
        scaffoldState = rememberScaffoldState(),
        diaryEventDates = listOf()
    )
}

