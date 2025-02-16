package com.foreverrafs.superdiary.list.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.usecase.DeleteDiaryUseCase
import com.foreverrafs.superdiary.domain.usecase.SearchDiaryByDateUseCase
import com.foreverrafs.superdiary.domain.usecase.SearchDiaryByEntryUseCase
import com.foreverrafs.superdiary.domain.usecase.UpdateDiaryUseCase
import com.foreverrafs.superdiary.list.DiaryFilters
import com.foreverrafs.superdiary.list.domain.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.utils.toInstant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
internal class DiaryListViewModel(
    getAllDiariesUseCase: GetAllDiariesUseCase,
    private val searchDiaryByEntryUseCase: SearchDiaryByEntryUseCase,
    private val searchDiaryByDateUseCase: SearchDiaryByDateUseCase,
    private val deleteDiaryUseCase: DeleteDiaryUseCase,
    private val updateDiaryUseCase: UpdateDiaryUseCase,
    private val logger: AggregateLogger,
) : ViewModel() {

    private val filters: MutableStateFlow<DiaryFilters> = MutableStateFlow(DiaryFilters())

    val state: StateFlow<DiaryListViewState> = filters
        .flatMapLatest { filters ->
            if (filters.entry.isNotEmpty() && filters.date != null) {
                return@flatMapLatest searchByDateAndEntry(
                    entry = filters.entry,
                    date = filters.date,
                )
            }

            if (filters.entry.isNotEmpty()) {
                return@flatMapLatest searchByEntry(entry = filters.entry)
            }

            if (filters.date != null) {
                return@flatMapLatest searchByDate(date = filters.date)
            }

            // No filter applied, return all the diaries
            getAllDiariesUseCase()
        }
        .map { result ->
            when (result) {
                is Result.Failure -> DiaryListViewState.Error(result.error)
                is Result.Success -> DiaryListViewState.Content(
                    result.data,
                    true,
                )
            }
        }
        .catch {
            emit(DiaryListViewState.Error(it))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = DiaryListViewState.Loading,
        )

    fun applyFilter(newFilters: DiaryFilters) {
        this.filters.update {
            newFilters
        }
    }

    private fun searchByEntry(entry: String): Flow<Result<List<Diary>>> =
        searchDiaryByEntryUseCase.invoke(entry)
            .map { Result.Success(it) }

    private fun searchByDate(date: LocalDate): Flow<Result<List<Diary>>> =
        searchDiaryByDateUseCase.invoke(date.toInstant())
            .map {
                Result.Success(it)
            }

    private fun searchByDateAndEntry(date: LocalDate, entry: String): Flow<Result<List<Diary>>> =
        searchDiaryByDateUseCase(date = date.toInstant())
            .map { list ->
                Result.Success(
                    list.filter {
                        it.entry.contains(entry)
                    },
                )
            }

    suspend fun deleteDiaries(diaries: List<Diary>): Boolean =
        when (val result = deleteDiaryUseCase(diaries)) {
            is Result.Success -> result.data == diaries.size
            is Result.Failure -> false
        }

    suspend fun toggleFavorite(diary: Diary): Boolean {
        val result = updateDiaryUseCase(
            diary.copy(
                isFavorite = !diary.isFavorite,
            ),
        )

        return when (result) {
            is Result.Failure -> {
                logger.e(Tag, result.error) {
                    "Error toggling favorite"
                }
                false
            }

            is Result.Success -> {
                logger.d(Tag) {
                    "Favorite toggled"
                }
                result.data
            }
        }
    }

    companion object {
        private val Tag = DiaryListViewModel::class.simpleName.orEmpty()
    }
}
