package com.foreverrafs.superdiary.android.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foreverrafs.superdiary.AndroidDatabaseDriver
import com.foreverrafs.superdiary.android.AppTheme
import com.foreverrafs.superdiary.android.components.DiaryList
import com.foreverrafs.superdiary.diary.Database
import com.foreverrafs.superdiary.diary.datasource.LocalDataSource
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.usecase.SearchDiaryUseCase
import com.ramcosta.composedestinations.annotation.Destination
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import io.github.boguszpawlowski.composecalendar.selection.SelectionMode
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toKotlinLocalDate
import java.time.format.TextStyle
import java.util.Locale

@Destination
@Composable
@AppNavGraph(start = false)
fun CalendarScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val dataSource = LocalDataSource(
        Database(AndroidDatabaseDriver(context))
    )

    val searchDiaryUseCase = SearchDiaryUseCase(dataSource)
    var diaries by remember {
        mutableStateOf(listOf<Diary>())
    }

    Content(
        modifier = modifier,
        onSearch = { date ->
            coroutineScope.launch {
                diaries = searchDiaryUseCase.searchByDate(date = date)
            }
        },
        diaries = diaries
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    onSearch: (date: LocalDate) -> Unit,
    diaries: List<Diary>
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(8.dp)
    ) {
        Calendar(onDateSelected = onSearch)
        DiaryList(diaries = diaries, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun Calendar(
    modifier: Modifier = Modifier,
    onDateSelected: (date: LocalDate) -> Unit,
) {
    val calendarState = rememberSelectableCalendarState(
        initialSelectionMode = SelectionMode.Single,
    )

    SelectableCalendar(
        dayContent = {
            val selected = calendarState.selectionState.isDateSelected(it.date)

            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(48.dp)
                    .clip(shape = CircleShape)
                    .clickable {
                        calendarState.selectionState.onDateSelected(it.date)
                        onDateSelected(it.date.toKotlinLocalDate())
                    }
                    .background(
                        color = if (selected) {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                alpha = 0.15f
                            )
                        } else {
                            Color.Transparent
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = it.date.dayOfMonth.toString()
                )
            }
        },
        calendarState = calendarState,
        daysOfWeekHeader = { daysOfWeek ->
            Row(modifier = modifier) {
                daysOfWeek.forEach { dayOfWeek ->
                    Text(
                        textAlign = TextAlign.Center,
                        text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentHeight()
                            .padding(bottom = 8.dp),
                    )
                }
            }
        },
        monthHeader = { monthState ->
            val currentMonth = monthState.currentMonth
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${
                        currentMonth.month.getDisplayName(
                            TextStyle.FULL,
                            Locale.getDefault()
                        )
                    } ${currentMonth.year}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )

                Row {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = null,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                calendarState.monthState.currentMonth =
                                    currentMonth.minusMonths(1)
                            }
                            .padding(8.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                calendarState.monthState.currentMonth =
                                    currentMonth.plusMonths(1)
                            }
                            .padding(8.dp)
                    )
                }
            }
        },
        showAdjacentMonths = false,
    )
}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        Surface {
            Content(onSearch = {}, diaries = listOf())
        }
    }
}
