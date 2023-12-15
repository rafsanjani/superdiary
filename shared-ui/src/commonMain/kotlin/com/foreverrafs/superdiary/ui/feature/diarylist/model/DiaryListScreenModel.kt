package com.foreverrafs.superdiary.ui.feature.diarylist.model

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.usecase.DeleteMultipleDiariesUseCase
import com.foreverrafs.superdiary.diary.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.diary.usecase.SearchDiaryByDateUseCase
import com.foreverrafs.superdiary.diary.usecase.SearchDiaryByEntryUseCase
import com.foreverrafs.superdiary.diary.usecase.UpdateDiaryUseCase
import com.foreverrafs.superdiary.diary.utils.toInstant
import com.foreverrafs.superdiary.ui.feature.diarylist.screen.DiaryListScreenState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class DiaryListScreenModel(
    private val getAllDiariesUseCase: GetAllDiariesUseCase,
    private val searchDiaryByEntryUseCase: SearchDiaryByEntryUseCase,
    private val searchDiaryByDateUseCase: SearchDiaryByDateUseCase,
    private val deleteMultipleDiariesUseCase: DeleteMultipleDiariesUseCase,
    private val updateDiaryUseCase: UpdateDiaryUseCase,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : StateScreenModel<DiaryListScreenState>(DiaryListScreenState.Loading) {

    fun observeDiaries() = screenModelScope.launch(coroutineDispatcher) {
        mutableState.update {
            DiaryListScreenState.Loading
        }

        getAllDiariesUseCase().collect { diaries ->
            mutableState.update {
                DiaryListScreenState.Content(
                    diaries = diaries,
                    filtered = false,
                )
            }
        }
    }

    fun filterByEntry(entry: String) = screenModelScope.launch(coroutineDispatcher) {
        searchDiaryByEntryUseCase(entry).collect { diaries ->
            mutableState.update {
                DiaryListScreenState.Content(
                    diaries = diaries,
                    filtered = true,
                )
            }
        }
    }

    fun filterByDate(date: LocalDate) = screenModelScope.launch(coroutineDispatcher) {
        searchDiaryByDateUseCase(date.toInstant()).collect { diaries ->
            mutableState.update {
                DiaryListScreenState.Content(
                    diaries = diaries,
                    filtered = true,
                )
            }
        }
    }

    fun filterByDateAndEntry(date: LocalDate, entry: String) =
        screenModelScope.launch(coroutineDispatcher) {
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
