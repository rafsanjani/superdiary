package com.foreverrafs.superdiary.list.presentation

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import com.foreverrafs.auth.AuthApi
import com.foreverrafs.auth.model.UserInfo
import com.foreverrafs.superdiary.common.coroutines.TestAppDispatchers
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.repository.DataSource
import com.foreverrafs.superdiary.domain.usecase.DeleteDiaryUseCase
import com.foreverrafs.superdiary.domain.usecase.SearchDiaryByDateUseCase
import com.foreverrafs.superdiary.domain.usecase.SearchDiaryByEntryUseCase
import com.foreverrafs.superdiary.domain.usecase.UpdateDiaryUseCase
import com.foreverrafs.superdiary.list.DiaryFilters
import com.foreverrafs.superdiary.list.domain.repository.DiaryListRepository
import com.foreverrafs.superdiary.list.domain.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.list.presentation.screen.list.DiaryListViewModel
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock

@OptIn(ExperimentalCoroutinesApi::class)
class DiaryListViewModelTest {

    private val dataSource: DataSource = mock<DataSource>()

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

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(TestAppDispatchers.main)

        every { dataSource.findByDate(any()) }.returns(
            flowOf(listOf(diary)),
        )

        every { dataSource.fetchAll() }
            .returns(flowOf(emptyList()))

        every { dataSource.find(any() as String) }.returns(
            flowOf(listOf(diary)),
        )

        everySuspend { repository.getAllDiaries() } returns flowOf(listOf(diary))

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
        every { dataSource.fetchAll() }.returns(flowOf(listOf(diary)))

        diaryListViewModel.state.test {
            val state = awaitItem()
            cancelAndIgnoreRemainingEvents()

            assertThat(state.isLoading).isTrue()
        }
    }

    @Test
    fun `Diary list - Error state is emitted when datasource fails`() = runTest {
        every { dataSource.fetchAll() }.returns(
            flow {
                throw IllegalArgumentException("Exception thrown in datasource")
            },
        )

        every { repository.getAllDiaries() }.returns(
            flow {
                throw IllegalArgumentException("Exception thrown in repository")
            },
        )

        diaryListViewModel.state.test {
            skipItems(1)
            delay(100)

            val state = awaitItem()
            assertThat(state.error).isNotNull()
        }
    }

    @Test
    fun `Diary list - Verify list gets loaded successfully`() = runTest {
        every { dataSource.fetchAll() }.returns(flowOf(listOf(diary)))

        diaryListViewModel.state.test {
            skipItems(1)
            val state = awaitItem()
            cancelAndIgnoreRemainingEvents()

            assertThat(state.diaries).isNotEmpty()
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

            assertThat(state.diaries).isNotEmpty()
        }
    }

    @Test
    fun `Filters - Apply filter with entry emits success state`() = runTest {
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

            assertThat(state.diaries).isNotEmpty()
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
