package com.foreverrafs.superdiary.ui.diarylist

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isInstanceOf
import com.foreverrafs.superdiary.TestAppDispatchers
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.repository.DataSource
import com.foreverrafs.superdiary.domain.usecase.DeleteDiaryUseCase
import com.foreverrafs.superdiary.domain.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.domain.usecase.SearchDiaryByDateUseCase
import com.foreverrafs.superdiary.domain.usecase.SearchDiaryByEntryUseCase
import com.foreverrafs.superdiary.domain.usecase.UpdateDiaryUseCase
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryFilters
import com.foreverrafs.superdiary.ui.feature.diarylist.model.DiaryListViewModel
import com.foreverrafs.superdiary.ui.feature.diarylist.screen.DiaryListViewState
import com.foreverrafs.superdiary.utils.toDate
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

    private lateinit var diaryListViewModel: DiaryListViewModel

    private val today = Clock.System.now()

    private val diary = Diary(
        entry = "today's diary",
        date = today,
        isFavorite = false,
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { dataSource.findByDate(any()) }.returns(
            flowOf(listOf(diary)),
        )

        every { dataSource.fetchAll() }
            .returns(flowOf(emptyList()))

        every { dataSource.find(any() as String) }.returns(
            flowOf(listOf(diary)),
        )

        diaryListViewModel = DiaryListViewModel(
            getAllDiariesUseCase = GetAllDiariesUseCase(dataSource, TestAppDispatchers),
            searchDiaryByEntryUseCase = SearchDiaryByEntryUseCase(dataSource, TestAppDispatchers),
            searchDiaryByDateUseCase = SearchDiaryByDateUseCase(dataSource, TestAppDispatchers),
            updateDiaryUseCase = UpdateDiaryUseCase(dataSource, TestAppDispatchers),
            deleteDiaryUseCase = DeleteDiaryUseCase(dataSource, TestAppDispatchers),
            logger = AggregateLogger(emptyList()),
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
            val state = awaitItem()
            cancelAndIgnoreRemainingEvents()

            assertThat(state).isInstanceOf<DiaryListViewState.Loading>()
        }
    }

    @Test
    fun `Verify diary list gets loaded successfully`() = runTest {
        every { dataSource.fetchAll() }.returns(flowOf(listOf(diary)))

        diaryListViewModel.state.test {
            skipItems(1)
            val state = awaitItem()
            cancelAndIgnoreRemainingEvents()

            assertThat(state).isInstanceOf<DiaryListViewState.Content>()
        }
    }

    @Test
    fun `Verify filter by date  emits success state`() = runTest {
        diaryListViewModel.state.test {
            diaryListViewModel.applyFilter(
                DiaryFilters(
                    date = today.toDate(),
                ),
            )

            skipItems(1)
            val state = awaitItem()

            assertThat(state).isInstanceOf<DiaryListViewState.Content>()
        }
    }

    @Test
    fun `Apply filter with entry emits success state`() = runTest {
        everySuspend { dataSource.find("entry") }.returns(
            flowOf(
                listOf(Diary("hello world")),
            ),
        )

        diaryListViewModel.state.test {
            diaryListViewModel.applyFilter(
                DiaryFilters(entry = "entry"),
            )
            skipItems(1)

            val state = awaitItem()

            assertThat(state).isInstanceOf<DiaryListViewState.Content>()
        }
    }

    @Test
    fun `Apply filter with entry and date returns Content state`() = runTest {
        diaryListViewModel.state.test {
            diaryListViewModel.applyFilter(
                DiaryFilters(
                    entry = "entry",
                    date = today.toDate(),
                ),
            )
            skipItems(1)
            val state = awaitItem()
            cancelAndIgnoreRemainingEvents()

            assertThat(state).isInstanceOf<DiaryListViewState.Content>()
        }
    }

    @Test
    fun `Deleting a diary calls dataSource delete with correct parameters`() = runTest {
        everySuspend {
            dataSource.delete(diaries = any())
        }.returns(1)

        diaryListViewModel.deleteDiaries(diaries = listOf())

        verifySuspend { dataSource.delete(diaries = any()) }
    }

    @Test
    fun `Toggle favorite updates diaries in datasource`() = runTest {
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
            skipItems(1)
            delay(100)

            val state = awaitItem()
            assertThat(state).isInstanceOf<DiaryListViewState.Error>()
        }
    }
}
