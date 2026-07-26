package com.foreverrafs.superdiary.list.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.components.diarylist.DiaryFilters
import com.foreverrafs.auth.AuthApi
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.usecase.SearchDiaryByDateUseCase
import com.foreverrafs.superdiary.domain.usecase.SearchDiaryByEntryUseCase
import com.foreverrafs.superdiary.domain.usecase.UpdateDiaryUseCase
import com.foreverrafs.superdiary.list.domain.usecase.DeleteDiaryUseCase
import com.foreverrafs.superdiary.list.domain.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.utils.toInstant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate

data class DiaryListScreenModel(
    val diaries: Flow<PagingData<Diary>> = flowOf(PagingData.empty()),
    val isLoading: Boolean,
    val isFiltered: Boolean = false,
    val avatarUrl: String? = null,
    val error: Throwable? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
internal class DiaryListViewModel(
    getAllDiariesUseCase: GetAllDiariesUseCase,
    private val searchDiaryByEntryUseCase: SearchDiaryByEntryUseCase,
    private val searchDiaryByDateUseCase: SearchDiaryByDateUseCase,
    private val deleteDiaryUseCase: DeleteDiaryUseCase,
    private val updateDiaryUseCase: UpdateDiaryUseCase,
    private val logger: AggregateLogger,
    private val authApi: AuthApi,
) : ViewModel() {

    private val filters: MutableStateFlow<DiaryFilters> = MutableStateFlow(DiaryFilters())

    private val diaries: Flow<PagingData<Diary>> = filters
        .flatMapLatest { filters ->
            if (filters.entry.isNotEmpty() && filters.date != null) {
                return@flatMapLatest searchByDateAndEntry(
                    entry = filters.entry,
                    date = filters.date!!,
                )
            }

            if (filters.entry.isNotEmpty()) {
                return@flatMapLatest searchByEntry(entry = filters.entry)
            }

            if (filters.date != null) {
                return@flatMapLatest searchByDate(date = filters.date!!)
            }

            // No filter applied, return all the diaries
            getAllDiariesUseCase()
        }
        .cachedIn(viewModelScope)

    val state: StateFlow<DiaryListScreenModel> = filters
        .map { filters ->
            DiaryListScreenModel(
                diaries = diaries,
                isFiltered = filters.entry.isNotEmpty() || filters.date != null,
                isLoading = false,
                avatarUrl = authApi.currentUserOrNull()?.avatarUrl,
            )
        }
        .catch {
            emit(DiaryListScreenModel(error = it, isLoading = false))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DiaryListScreenModel(
                diaries = diaries,
                isFiltered = false,
                isLoading = true,
                avatarUrl = authApi.currentUserOrNull()?.avatarUrl,
            ),
        )

    fun applyFilter(newFilters: DiaryFilters) {
        this.filters.update {
            newFilters
        }
    }

    private fun searchByEntry(entry: String): Flow<PagingData<Diary>> =
        searchDiaryByEntryUseCase.invoke(entry)

    private fun searchByDate(date: LocalDate): Flow<PagingData<Diary>> =
        searchDiaryByDateUseCase.invoke(date.toInstant())

    private fun searchByDateAndEntry(
        date: LocalDate,
        entry: String,
    ): Flow<PagingData<Diary>> =
        searchDiaryByDateUseCase(
            entry = entry,
            date = date.toInstant(),
        )

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
                logger.e(tag = Tag, throwable = result.error) {
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
