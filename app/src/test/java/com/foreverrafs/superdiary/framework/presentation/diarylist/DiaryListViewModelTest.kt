package com.foreverrafs.superdiary.framework.presentation.diarylist

import app.cash.turbine.test
import com.foreverrafs.domain.feature_diary.Result
import com.foreverrafs.domain.feature_diary.repository.Repository
import com.foreverrafs.domain.feature_diary.usecase.DeleteDiaryUseCase
import com.foreverrafs.domain.feature_diary.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.ui.feature_diary.diarylist.DiaryListState
import com.foreverrafs.superdiary.ui.feature_diary.diarylist.DiaryListViewModel
import com.foreverrafs.superdiary.util.TestData
import com.foreverrafs.superdiary.util.rules.CoroutineTestRule
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.Month
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
class DiaryListViewModelTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()
    private val repository: Repository = mockk()

    private val diaryListViewModel = DiaryListViewModel(
        dispatcher = coroutineRule.testDispatcher,
        fetchAllDiariesUseCase = GetAllDiariesUseCase(repository),
        deleteDiaryUseCase = DeleteDiaryUseCase(repository)
    )

    @Before
    fun mockResponses() {
        coEvery {
            repository.getAllDiaries()
        } returns flowOf(
            Result.Success(
                data = TestData.testDiaries
            )
        )
    }

    @Test
    fun `verify initial viewState is null`() = runTest {
        diaryListViewModel.viewState.test {
            val initialState = awaitItem()
            cancelAndConsumeRemainingEvents()

            assertThat(initialState).isNull()
        }
    }

    @Test
    fun `verify final viewState transitions from null to DiaryListState`() = runTest {
        diaryListViewModel.viewState.test {
            val initialState = awaitItem()
            val finalViewState = awaitItem()
            cancelAndConsumeRemainingEvents()

            assertThat(initialState).isNull()
            assertThat(finalViewState).isInstanceOf(DiaryListState::class.java)
        }
    }

    @Test
    fun `fetch all diaries, confirm state and list`() = runTest {
        mockResponses()

        diaryListViewModel.viewState.test {
            val initialState = awaitItem()
            val finalState = awaitItem()
            cancelAndConsumeRemainingEvents()

            assertThat(initialState).isNull()
            assertThat(finalState).isInstanceOf(DiaryListState.Loaded::class.java)
            assertThat((finalState as DiaryListState.Loaded).list).isNotEmpty()
        }
    }

    @Test
    fun `get diary for date confirm fetched`() = runTest {
        mockResponses()

        diaryListViewModel.viewState.test {
            // These states are emitted during ViewModel init
            awaitItem()
            awaitItem()

            diaryListViewModel.getDiariesForDate(LocalDate.of(2022, Month.JANUARY, 1))

            val filteredState = expectMostRecentItem()

            assertThat(filteredState).isNotNull()
            assertThat(filteredState).isInstanceOf(DiaryListState.Loaded::class.java)

            val data = filteredState as DiaryListState.Loaded

            assertThat(data.list).isNotEmpty()
            assertThat(data.filtered).isTrue()
        }
    }

    @Test
    fun `get diary for date confirm empty`() = runTest {
        mockResponses()

        diaryListViewModel.getDiariesForDate(LocalDate.of(2022, Month.JUNE, 1))

        diaryListViewModel.viewState.test {
            val filteredState = expectMostRecentItem()
            cancelAndConsumeRemainingEvents()

            assertThat(filteredState).isNotNull()
            assertThat(filteredState).isInstanceOf(DiaryListState.Loaded::class.java)

            val data = filteredState as DiaryListState.Loaded

            assertThat(data.list).isEmpty()
            assertThat(data.filtered).isTrue()
        }
    }
}