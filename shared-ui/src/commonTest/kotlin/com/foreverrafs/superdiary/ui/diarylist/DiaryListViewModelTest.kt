package com.foreverrafs.superdiary.ui.diarylist

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isInstanceOf
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.usecase.DeleteMultipleDiariesUseCase
import com.foreverrafs.superdiary.diary.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.diary.usecase.SearchDiaryByDateUseCase
import com.foreverrafs.superdiary.diary.usecase.SearchDiaryByEntryUseCase
import com.foreverrafs.superdiary.diary.usecase.UpdateDiaryUseCase
import com.foreverrafs.superdiary.diary.utils.toDate
import com.foreverrafs.superdiary.ui.feature.diarylist.model.DiaryListViewModel
import com.foreverrafs.superdiary.ui.feature.diarylist.screen.DiaryListViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import org.kodein.mock.Mock
import org.kodein.mock.tests.TestsWithMocks
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DiaryListViewModelTest : TestsWithMocks() {
    override fun setUpMocks() = injectMocks(mocker)

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    lateinit var dataSource: DataSource

    private lateinit var diaryListViewModel: DiaryListViewModel

    private val today = Clock.System.now()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val diary = Diary(
            entry = "",
            date = today,
            isFavorite = false,
        )

        every { dataSource.fetchAll() } returns flowOf(listOf(diary))
        every { dataSource.findByDate(isAny()) } returns flowOf(listOf(diary))
        every { dataSource.find(isAny()) } returns flowOf(listOf(diary))

        diaryListViewModel = DiaryListViewModel(
            getAllDiariesUseCase = GetAllDiariesUseCase(dataSource),
            searchDiaryByEntryUseCase = SearchDiaryByEntryUseCase(dataSource),
            searchDiaryByDateUseCase = SearchDiaryByDateUseCase(dataSource),
            updateDiaryUseCase = UpdateDiaryUseCase(dataSource),
            deleteMultipleDiariesUseCase = DeleteMultipleDiariesUseCase(dataSource),
        )
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Verify diary list screen starts from loading state`() = runTest {
        diaryListViewModel.state.test {
            diaryListViewModel.observeDiaries()
            val state = awaitItem()
            cancelAndIgnoreRemainingEvents()

            assertThat(state).isInstanceOf<DiaryListViewState.Loading>()
        }
    }

    @Test
    fun `Verify diary list gets loaded successfully`() = runTest {
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
        everySuspending {
            dataSource.delete(diaries = isAny())
        } returns 1

        diaryListViewModel.deleteDiaries(diaries = listOf())

        verifyWithSuspend { dataSource.delete(diaries = isAny()) }
    }

    @Test
    fun `Verify toggle favorite actually updates the entry`() = runTest {
        everySuspending {
            dataSource.update(diary = isAny())
        } returns 1

        diaryListViewModel.toggleFavorite(diary = Diary("hello-boss"))

        verifyWithSuspend { dataSource.update(isAny()) }
    }
}
