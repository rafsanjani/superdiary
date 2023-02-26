package com.foreverrafs.superdiary.android.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.foreverrafs.datepicker.DatePickerTimeline
import com.foreverrafs.datepicker.state.rememberDatePickerState
import com.foreverrafs.superdiary.AndroidDatabaseDriver
import com.foreverrafs.superdiary.Database
import com.foreverrafs.superdiary.android.AppTheme
import com.foreverrafs.superdiary.diary.datasource.LocalDataSource
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.usecase.AddDiaryUseCase
import com.foreverrafs.superdiary.diary.usecase.DeleteDiaryUseCase
import com.foreverrafs.superdiary.diary.usecase.GetAllDiariesUseCase
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AppNavGraph(start = true)
@Destination
@Composable
fun DiaryListScreen() {
    Content()
}

@Composable
private fun Content() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Super Diary",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
        )
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TextField(
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = null)
                    },
                    trailingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    },
                    placeholder = {
                        Text(
                            text = "Search diary by phrase or date...",
                            textAlign = TextAlign.Center,
                        )
                    },
                    value = "",
                    onValueChange = {},
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                )
            },
        ) { padding ->
            val dataSource = LocalDataSource(
                Database(
                    AndroidDatabaseDriver(LocalContext.current),
                ),
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val datePickerState = rememberDatePickerState()
                var selectedDate by remember {
                    mutableStateOf(LocalDate.now())
                }

                val useCase = GetAllDiariesUseCase(dataSource)
                val addDiary = AddDiaryUseCase(dataSource)
                val deleteDiary = DeleteDiaryUseCase(dataSource)

                val diaries by useCase.diaries.collectAsState(listOf())
                LaunchedEffect(Unit) {
                    deleteDiary.deleteAll()
                }
                var entryNumber by remember { mutableStateOf(0) }

                LaunchedEffect(selectedDate) {
                    addDiary(
                        Diary(
                            entry = "Diary Number ${++entryNumber}",
                            date = DateTimeFormatter.ofPattern("MMM dd yyyy").format(selectedDate)
                                .format(selectedDate),
                        ),
                    )
                }

                Text(
                    modifier = Modifier
                        .padding(padding)
                        .clickable {
                            datePickerState.smoothScrollToDate(
                                LocalDate.now(),
                            )
                        }
                        .padding(16.dp),
                    text = DateTimeFormatter.ofPattern("MMM dd yyyy").format(selectedDate)
                        .format(selectedDate),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                )

                DatePickerTimeline(
                    state = datePickerState,
                    onDateSelected = {
                        selectedDate = it
                    },
                    backgroundColor = MaterialTheme.colorScheme.background,
                    dateTextColor = MaterialTheme.colorScheme.onBackground,
                    selectedBackgroundColor = Color.Black.copy(alpha = 0.5f),
                    selectedTextColor = MaterialTheme.colorScheme.onBackground,
                )

                val coroutineScope = rememberCoroutineScope()

                Button(onClick = {
                    coroutineScope.launch {
                        deleteDiary.deleteDiary(Diary(id = 1L, date = "", entry = ""))
                    }
                }) {
                    Text("Delete")
                }

                LazyColumn {
                    items(items = diaries) {
                        Text(text = it.entry)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun DarkPreview() {
    AppTheme(darkTheme = true) {
        Surface {
            Content()
        }
    }
}

@Preview
@Composable
private fun LightPreview() {
    AppTheme(darkTheme = false) {
        Surface {
            Content()
        }
    }
}
