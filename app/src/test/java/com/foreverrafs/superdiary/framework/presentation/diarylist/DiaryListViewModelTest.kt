package com.foreverrafs.superdiary.framework.presentation.diarylist

import app.cash.turbine.test
import com.foreverrafs.superdiary.business.data.DependenciesInjector
import com.foreverrafs.superdiary.framework.presentation.diarylist.state.DiaryListState
import com.foreverrafs.superdiary.util.rules.CoroutineTestRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
class DiaryListViewModelTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private val diaryListViewModel = DiaryListViewModel(
        dispatcher = coroutineRule.testDispatcher,
        fetchAllDiariesUseCase = DependenciesInjector.provideGetAllDiaryUseCase(),
        deleteDiaryUseCase = DependenciesInjector.provideDeleteDiaryUseCase()
    )

    @Test
    fun `confirm all diaries have been fetched`() = runBlockingTest {
        val items = diaryListViewModel.allDiaries
        assertThat(items).isNotEmpty()

        diaryListViewModel.viewState.test {
            val data = expectItem()

            assertThat(data).isInstanceOf(DiaryListState.DiaryList::class.java)
            assertThat((data as DiaryListState.DiaryList).list).isNotEmpty()
        }
    }

    @Test
    fun `get diary for date confirm fetched`() = runBlockingTest {
        diaryListViewModel.getDiariesForDate(LocalDate.of(2020, 12, 28))

        diaryListViewModel.viewState.test {
            val data = expectItem()

            assertThat(data).isInstanceOf(DiaryListState.DiaryList::class.java)
            assertThat((data as DiaryListState.DiaryList).list).isNotEmpty()
        }
    }

    @Test
    fun `get diary for date confirm empty`() = runBlockingTest {
        diaryListViewModel.getDiariesForDate(LocalDate.of(2020, 1, 1))

        diaryListViewModel.viewState.test {
            val data = expectItem()

            assertThat(data).isInstanceOf(DiaryListState.Empty::class.java)
        }
    }


}