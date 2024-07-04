package com.foreverrafs.superdiary.ui.diarylist

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isInstanceOf
import com.foreverrafs.superdiary.TestAppDispatchers
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.data.usecase.DeleteDiaryUseCase
import com.foreverrafs.superdiary.data.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.data.usecase.SearchDiaryByDateUseCase
import com.foreverrafs.superdiary.data.usecase.SearchDiaryByEntryUseCase
import com.foreverrafs.superdiary.data.usecase.UpdateDiaryUseCase
import com.foreverrafs.superdiary.data.utils.toDate
import com.foreverrafs.superdiary.ui.feature.diarylist.model.DiaryListViewModel
import com.foreverrafs.superdiary.ui.feature.diarylist.screen.DiaryListViewState
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock

@OptIn(ExperimentalCoroutinesApi::class)
class DiaryListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val dataSource: DataSource = mock<DataSource>()

    private val diaryListViewModel = DiaryListViewModel(
        getAllDiariesUseCase = GetAllDiariesUseCase(dataSource, TestAppDispatchers),
        searchDiaryByEntryUseCase = SearchDiaryByEntryUseCase(dataSource, TestAppDispatchers),
        searchDiaryByDateUseCase = SearchDiaryByDateUseCase(dataSource, TestAppDispatchers),
        updateDiaryUseCase = UpdateDiaryUseCase(dataSource, TestAppDispatchers),
        deleteDiaryUseCase = DeleteDiaryUseCase(dataSource, TestAppDispatchers),
        logger = AggregateLogger(emptyList()),
    )

    private val today = Clock.System.now()

    private val diary = Diary(
        entry = "",
        date = today,
        isFavorite = false,
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { dataSource.findByDate(any()) }.returns(
            flowOf(listOf(diary)),
        )

        every { dataSource.find(any() as String) }.returns(
            flowOf(listOf(diary)),
        )
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Verify diary list screen starts from loading state`() = runTest {
        every { dataSource.fetchAll() }.returns(flowOf(listOf(diary)))

        diaryListViewModel.state.test {
            diaryListViewModel.observeDiaries()
            val state = awaitItem()
            cancelAndIgnoreRemainingEvents()

            assertThat(state).isInstanceOf<DiaryListViewState.Loading>()
        }
    }

    @Test
    fun `Verify diary list gets loaded successfully`() = runTest {
        every { dataSource.fetchAll() }.returns(flowOf(listOf(diary)))

        diaryListViewModel.state.test {
            diaryListViewModel.observeDiaries()

            // Consume and skip loading state
            skipItems(1)
            val state = awaitItem()
            cancelAndIgnoreRemainingEvents()

            assertThat(state).isInstanceOf<DiaryListViewState.Content>()
        }
    }

    @Test
    fun `Verify filter by date  emits success state`() = runTest {
        diaryListViewModel.state.test {
            diaryListViewModel.filterByDate(today.toDate())

            skipItems(1)
            val state = awaitItem()

            assertThat(state).isInstanceOf<DiaryListViewState.Content>()
        }
    }

    @Test
    fun `Verify filter by entry  emits success state`() = runTest {
        diaryListViewModel.state.test {
            diaryListViewModel.filterByEntry("entry")
            skipItems(1)
            val state = awaitItem()

            assertThat(state).isInstanceOf<DiaryListViewState.Content>()
        }
    }

    @Test
    fun `Verify filter by date and entry  emits success state`() = runTest {
        diaryListViewModel.state.test {
            diaryListViewModel.filterByDateAndEntry(
                entry = "entry",
                date = today.toDate(),
            )
            skipItems(1)
            val state = awaitItem()
            cancelAndIgnoreRemainingEvents()

            assertThat(state).isInstanceOf<DiaryListViewState.Content>()
        }
    }

    @Test
    fun `Verify delete diaries actually deletes them`() = runTest {
        everySuspend {
            dataSource.delete(diaries = any())
        }.returns(1)

        diaryListViewModel.deleteDiaries(diaries = listOf())

        verifySuspend { dataSource.delete(diaries = any()) }
    }

    @Test
    fun `Verify toggle favorite actually updates the entry`() = runTest {
        everySuspend {
            dataSource.update(diary = any())
        }.returns(1)

        diaryListViewModel.toggleFavorite(diary = Diary("hello-boss"))

        verifySuspend { dataSource.update(any()) }
    }

    @Test
    fun `Verify error screen is shown when an error occurs`() = runTest {
        every { dataSource.fetchAll() }.returns(
            flow {
                throw IllegalArgumentException("Exception thrown in datasource")
            },
        )

        diaryListViewModel.state.test {
            diaryListViewModel.observeDiaries()
            delay(100)
            // Skip loading state
            skipItems(1)

            val state = awaitItem()
            assertThat(state).isInstanceOf<DiaryListViewState.Error>()
        }
    }
}
