package com.foreverrafs.superdiary.android.screens

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.diary.usecase.SearchDiaryUseCase
import com.foreverrafs.superdiary.ui.AppTheme
import com.foreverrafs.superdiary.ui.components.DiaryList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import java.time.LocalDate
import kotlin.random.Random

typealias DiaryTimelineScreen = @Composable () -> Unit

@Composable
@Inject
fun DiaryTimelineScreen(
    getDiariesUseCase: GetAllDiariesUseCase,
    searchDiaryUseCase: SearchDiaryUseCase,
) {
    val coroutineScope = rememberCoroutineScope()

    val diaries by getDiariesUseCase.diaries.collectAsState(initial = listOf())
    var searchDiaryResults by remember {
        mutableStateOf(listOf<Diary>())
    }

    Content(
        diaries = diaries,
        onSearchQueryChange = {
            coroutineScope.launch {
                searchDiaryResults = searchDiaryUseCase.searchByEntry(entry = it)
            }
        },
        filteredDiaries = searchDiaryResults,
    )
}

@Composable
private fun Content(
    diaries: List<Diary>,
    filteredDiaries: List<Diary>,
    onSearchQueryChange: (query: String) -> Unit,
) {
    val modalBottomSheetState =
        rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            confirmValueChange = { false },
        )

    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheetLayout(
        sheetContent = {
            Column(
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.background)
                    .clickable {
                        coroutineScope.launch {
                            modalBottomSheetState.hide()
                        }
                    },
            ) {
            }
        },
        sheetState = modalBottomSheetState,
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            modalBottomSheetState.show()
                        }
                    },
                    shape = CircleShape,
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                }
            },
            isFloatingActionButtonDocked = true,
            backgroundColor = MaterialTheme.colorScheme.background,
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                SearchField(
                    modifier = Modifier.align(Alignment.TopCenter),
                    onQueryChange = onSearchQueryChange,
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.height(50.dp))

                    DiaryList(
                        diaries = filteredDiaries.ifEmpty { diaries },
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchField(
    modifier: Modifier = Modifier,
    onQueryChange: (query: String) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    var query by remember {
        mutableStateOf("")
    }

    LaunchedEffect(query) {
        // a 1 second debounce
        delay(500)
        onQueryChange(query)
    }

    var active by rememberSaveable {
        mutableStateOf(false)
    }

    fun closeSearchBar() {
        focusManager.clearFocus()
        active = false
    }

    var isFocused by remember {
        mutableStateOf(false)
    }

    DockedSearchBar(
        modifier = modifier
            .onFocusChanged {
                isFocused = it.isFocused
            }
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        query = query,
        onQueryChange = {
            query = it
        },
        onSearch = { closeSearchBar() },
        active = false, // To prevent the searchbar from showing suggestions
        onActiveChange = {},
        placeholder = { Text("Search diary...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            Icon(
                modifier = Modifier.clickable { },
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
            )
        },
        content = {},
    )

    BackHandler(isFocused) {
        focusManager.clearFocus(true)
    }
}

@Preview(
    name = "Night Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Preview(
    name = "Day Mode",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
private fun Preview() {
    AppTheme {
        Surface {
            Content(
                diaries = (0..30).map {
                    Diary(
                        id = Random.nextLong(),
                        entry = "Test Diary",
                        date = LocalDate.now().minusDays(it.toLong()).toString(),
                    )
                },
                onSearchQueryChange = {},
                filteredDiaries = listOf(),
            )
        }
    }
}
