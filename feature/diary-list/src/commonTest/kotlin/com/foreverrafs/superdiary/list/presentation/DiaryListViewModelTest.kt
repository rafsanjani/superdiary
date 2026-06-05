package com.foreverrafs.superdiary.list.presentation

import androidx.paging.PagingData
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.components.diarylist.DiaryFilters
import com.foreverrafs.auth.AuthApi
import com.foreverrafs.auth.model.UserInfo
import com.foreverrafs.superdiary.common.coroutines.TestAppDispatchers
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.repository.DataSource
import com.foreverrafs.superdiary.domain.usecase.SearchDiaryByDateUseCase
import com.foreverrafs.superdiary.domain.usecase.SearchDiaryByEntryUseCase
import com.foreverrafs.superdiary.domain.usecase.UpdateDiaryUseCase
import com.foreverrafs.superdiary.list.domain.repository.DiaryListRepository
import com.foreverrafs.superdiary.list.domain.usecase.DeleteDiaryUseCase
import com.foreverrafs.superdiary.list.domain.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.list.presentation.list.DiaryListViewModel
import com.foreverrafs.superdiary.utils.toDate
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
class DiaryListViewModelTest {

    private val dataSource: DataSource = mock()

    private lateinit var diaryListViewModel: DiaryListViewModel

    private val repository: DiaryListRepository = mock()

    private val authApi: AuthApi = mock {
        every { currentUserOrNull() }.returns(
            UserInfo(
                id = "user-id",
                name = "user-name",
                avatarUrl = "",
                email = "email",
                uniqueEmail = "unique_email",
            ),
        )
    }

    private val today = Clock.System.now()

    private val diary = Diary(
        entry = "today's diary",
        date = today,
        isFavorite = false,
    )

    @OptIn(ExperimentalTime::class)
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())

        every { dataSource.findByDatePaged(any()) }.returns(
            flowOf(PagingData.from(listOf(diary))),
        )

        every { dataSource.fetchAllPaged() }
            .returns(flowOf(PagingData.empty()))

        every { dataSource.findPaged(entry = any()) }.returns(
            flowOf(PagingData.from(listOf(diary))),
        )
        every { dataSource.findPaged(from = any(), to = any()) }.returns(
            flowOf(PagingData.from(listOf(diary))),
        )
        every { dataSource.findPaged(entry = any(), from = any(), to = any()) }.returns(
            flowOf(PagingData.from(listOf(diary))),
        )

        every { repository.getAllDiaries() } returns flowOf(PagingData.from(listOf(diary)))

        diaryListViewModel = DiaryListViewModel(
            getAllDiariesUseCase = GetAllDiariesUseCase(repository = repository),
            searchDiaryByEntryUseCase = SearchDiaryByEntryUseCase(dataSource, TestAppDispatchers),
            searchDiaryByDateUseCase = SearchDiaryByDateUseCase(dataSource, TestAppDispatchers),
            updateDiaryUseCase = UpdateDiaryUseCase(dataSource, TestAppDispatchers),
            deleteDiaryUseCase = DeleteDiaryUseCase(dataSource, TestAppDispatchers),
            logger = AggregateLogger(emptyList()),
            authApi = authApi,
        )
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Diary list - Verify screen starts from loading state`() = runTest {
        diaryListViewModel.state.test {
            val state = awaitItem()
            cancelAndIgnoreRemainingEvents()

            assertThat(state.isLoading).isTrue()
        }
    }

    @Test
    fun `Diary list - Verify list gets loaded successfully`() = runTest {
        diaryListViewModel.state.test {
            skipItems(1)
            val state = awaitItem()
            cancelAndIgnoreRemainingEvents()

            assertThat(state.isLoading).isFalse()
        }
    }

    @Test
    fun `Filters - Verify filter by date emits success state`() = runTest {
        diaryListViewModel.state.test {
            diaryListViewModel.applyFilter(
                DiaryFilters(
                    date = today.toDate(),
                ),
            )

            skipItems(1)
            val state = awaitItem()

            assertThat(state.isFiltered).isTrue()
        }
    }

    @Test
    fun `Filters - Apply filter with entry emits success state`() = runTest {
        every { dataSource.findPaged("entry") }.returns(
            flowOf(
                PagingData.from(listOf(Diary("hello world"))),
            ),
        )

        diaryListViewModel.state.test {
            diaryListViewModel.applyFilter(
                DiaryFilters(entry = "entry"),
            )
            skipItems(1)

            val state = awaitItem()

            assertThat(state.isFiltered).isTrue()
        }
    }

    @Test
    fun `Filters - Apply filter with entry and date returns Content state`() = runTest {
        diaryListViewModel.state.test {
            diaryListViewModel.applyFilter(
                newFilters = DiaryFilters(
                    entry = "entry",
                    date = today.toDate(),
                ),
            )
            skipItems(1)
            val state = awaitItem()

            cancelAndIgnoreRemainingEvents()

            assertThat(state.isFiltered).isTrue()
        }
    }

    @Test
    fun `Delete entry - Operation invokes datasource delete with correct params`() = runTest {
        everySuspend {
            dataSource.delete(diaries = any())
        }.returns(1)

        diaryListViewModel.deleteDiaries(diaries = listOf())

        verifySuspend { dataSource.delete(diaries = any()) }
    }

    @Test
    fun `Delete entry - Operation should fail when datasource fails`() = runTest {
        everySuspend {
            dataSource.delete(diaries = any())
        }.throws(Exception("error deleting diary entry"))

        val result = diaryListViewModel.deleteDiaries(diaries = listOf())

        verifySuspend { dataSource.delete(diaries = any()) }
        assertThat(result).isFalse()
    }

    @Test
    fun `Favorite - Toggling favorite updates datasource`() = runTest {
        everySuspend {
            dataSource.update(diary = any())
        }.returns(1)

        val result = diaryListViewModel.toggleFavorite(diary = Diary("hello-boss"))

        verifySuspend { dataSource.update(any()) }
        assertThat(result).isTrue()
    }

    @Test
    fun `Favorite - Toggling favorite fails when datasource fails`() = runTest {
        everySuspend {
            dataSource.update(diary = any())
        }.throws(Exception("Error toggling favorite diary"))

        val result = diaryListViewModel.toggleFavorite(diary = Diary("hello-boss"))

        verifySuspend { dataSource.update(any()) }
        assertThat(result).isFalse()
    }
}
