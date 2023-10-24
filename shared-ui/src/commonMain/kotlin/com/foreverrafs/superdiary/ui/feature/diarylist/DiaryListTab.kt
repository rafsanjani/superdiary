package com.foreverrafs.superdiary.ui.feature.diarylist

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.usecase.DeleteMultipleDiariesUseCase
import com.foreverrafs.superdiary.diary.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.diary.usecase.SearchDiaryByDateUseCase
import com.foreverrafs.superdiary.diary.usecase.SearchDiaryByEntryUseCase
import com.foreverrafs.superdiary.diary.usecase.UpdateDiaryUseCase
import com.foreverrafs.superdiary.diary.utils.toInstant
import com.foreverrafs.superdiary.ui.LocalSnackbarHostState
import com.foreverrafs.superdiary.ui.feature.creatediary.CreateDiaryScreen
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

object DiaryListTab : Screen {

    @Composable
    override fun Content() {
        val screenModel: DiaryListScreenModel = getScreenModel()
        val screenState by screenModel.state.collectAsState()

        var diaryFilters by rememberSaveable(stateSaver = DiaryFilters.Saver) {
            mutableStateOf(DiaryFilters())
        }

        observeFilterEvents(diaryFilters, screenModel)

        val navigator = LocalNavigator.currentOrThrow
        val snackbarHostState = LocalSnackbarHostState.current

        val diaryListActions = remember {
            DiaryListActions(
                onAddEntry = {
                    navigator.push(
                        CreateDiaryScreen,
                    )
                },
                onDeleteDiaries = screenModel::deleteDiaries,
                onApplyFilters = {
                    diaryFilters = it
                },
                onToggleFavorite = screenModel::toggleFavorite,
            )
        }

        DiaryListScreen(
            modifier = Modifier.fillMaxSize(),
            state = screenState,
            showSearchBar = true,
            diaryFilters = diaryFilters,
            diaryListActions = diaryListActions,
            snackbarHostState = snackbarHostState,
        )
    }

    @Composable
    private fun observeFilterEvents(
        diaryFilters: DiaryFilters,
        screenModel: DiaryListScreenModel,
    ) {
        LaunchedEffect(diaryFilters) {
            // Filter by entry only
            if (diaryFilters.entry.isNotEmpty() && diaryFilters.date == null) {
                screenModel.filterByEntry(diaryFilters.entry)
                return@LaunchedEffect
            }

            // Filter by date only
            if (diaryFilters.date != null && diaryFilters.entry.isEmpty()) {
                screenModel.filterByDate(diaryFilters.date)
                return@LaunchedEffect
            }

            // Filter by both date and entry
            if (diaryFilters.date != null && diaryFilters.entry.isNotEmpty()) {
                screenModel.filterByDateAndEntry(diaryFilters.date, diaryFilters.entry)
                return@LaunchedEffect
            }

            // No filter applied
            screenModel.observeDiaries()
        }
    }
}

class DiaryListScreenModel(
    private val getAllDiariesUseCase: GetAllDiariesUseCase,
    private val searchDiaryByEntryUseCase: SearchDiaryByEntryUseCase,
    private val searchDiaryByDateUseCase: SearchDiaryByDateUseCase,
    private val deleteMultipleDiariesUseCase: DeleteMultipleDiariesUseCase,
    private val updateDiaryUseCase: UpdateDiaryUseCase,
) : StateScreenModel<DiaryListScreenState>(DiaryListScreenState.Loading) {

    init {
        observeDiaries()
    }

    fun observeDiaries() = screenModelScope.launch {
        getAllDiariesUseCase().collect { diaries ->
            mutableState.update {
                DiaryListScreenState.Content(
                    diaries = diaries,
                    filtered = false,
                )
            }
        }
    }

    fun filterByEntry(entry: String) = screenModelScope.launch {
        searchDiaryByEntryUseCase(entry).collect { diaries ->
            mutableState.update {
                DiaryListScreenState.Content(
                    diaries = diaries,
                    filtered = true,
                )
            }
        }
    }

    fun filterByDate(date: LocalDate) = screenModelScope.launch {
        searchDiaryByDateUseCase(date.toInstant()).collect { diaries ->
            mutableState.update {
                DiaryListScreenState.Content(
                    diaries = diaries,
                    filtered = true,
                )
            }
        }
    }

    fun filterByDateAndEntry(date: LocalDate, entry: String) = screenModelScope.launch {
        searchDiaryByDateUseCase(date.toInstant()).collect { diaries ->
            mutableState.update {
                DiaryListScreenState.Content(
                    diaries = diaries.filter { it.entry.contains(entry, false) },
                    filtered = true,
                )
            }
        }
    }

    suspend fun deleteDiaries(diaries: List<Diary>): Boolean {
        val affectedRows = deleteMultipleDiariesUseCase(diaries)
        return affectedRows == diaries.size
    }

    suspend fun toggleFavorite(diary: Diary): Boolean {
        return updateDiaryUseCase(
            diary.copy(
                isFavorite = !diary.isFavorite,
            ),
        )
    }
}
