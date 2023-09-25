package com.foreverrafs.superdiary.android.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.usecase.SearchDiaryUseCase
import com.foreverrafs.superdiary.ui.AppTheme
import com.foreverrafs.superdiary.ui.components.DiaryList
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import me.tatarka.inject.annotations.Inject

typealias CalendarScreen = @Composable () -> Unit

@Composable
@Inject
fun CalendarScreen(
    modifier: Modifier = Modifier,
    searchDiaryUseCase: SearchDiaryUseCase,
) {
    val coroutineScope = rememberCoroutineScope()

    var diaries by remember {
        mutableStateOf(listOf<Diary>())
    }

    Content(
        modifier = modifier.fillMaxSize(),
        onSearch = { date ->
            coroutineScope.launch {
                diaries = searchDiaryUseCase.searchByDate(date = date)
            }
        },
        diaries = diaries,
    )
}

@Composable
@Suppress("UnusedParameter")
private fun Content(
    modifier: Modifier = Modifier,
    onSearch: (date: LocalDate) -> Unit,
    diaries: List<Diary>,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(8.dp),
    ) {
//        Calendar(onDateSelected = onSearch)
        DiaryList(diaries = diaries, modifier = Modifier.weight(1f))
    }
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
