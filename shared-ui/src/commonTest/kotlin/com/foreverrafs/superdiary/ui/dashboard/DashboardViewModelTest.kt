package com.foreverrafs.superdiary.ui.dashboard

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.diaryai.DiaryAI
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.model.WeeklySummary
import com.foreverrafs.superdiary.diary.usecase.AddWeeklySummaryUseCase
import com.foreverrafs.superdiary.diary.usecase.CalculateStreakUseCase
import com.foreverrafs.superdiary.diary.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.diary.usecase.GetWeeklySummaryUseCase
import com.foreverrafs.superdiary.ui.feature.dashboard.DashboardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import org.kodein.mock.Mock
import org.kodein.mock.tests.TestsWithMocks
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardScreenModelTest : TestsWithMocks() {
    override fun setUpMocks() = injectMocks(mocker)

    @Mock
    lateinit var dataSource: DataSource

    private lateinit var dashboardViewModel: DashboardViewModel

    @Mock
    lateinit var diaryAI: DiaryAI

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        dashboardViewModel = DashboardViewModel(
            getAllDiariesUseCase = GetAllDiariesUseCase(dataSource),
            calculateStreakUseCase = CalculateStreakUseCase(),
            addWeeklySummaryUseCase = AddWeeklySummaryUseCase(dataSource),
            getWeeklySummaryUseCase = GetWeeklySummaryUseCase(dataSource),
            diaryAI = diaryAI,
        )
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Should load dashboard from loading state`() = runTest {
        dashboardViewModel.state.test {
            val state = awaitItem()

            assertThat(state).isInstanceOf<DashboardViewModel.DashboardScreenState.Loading>()
        }
    }

    @Test
    fun `Should generate weekly summary if diaries isn't empty`() = runTest {
        every { dataSource.fetchAll() } returns flowOf(listOf(Diary("Hello World")))
        every { dataSource.getWeeklySummary() } returns WeeklySummary("This is your summary for the week")

        dashboardViewModel.state.test {
            dashboardViewModel.loadDashboardContent()
            // Skip the loading state and the initial success state
            skipItems(2)

            val state = awaitItem()
            cancelAndIgnoreRemainingEvents()

            assertThat(state).isInstanceOf<DashboardViewModel.DashboardScreenState.Content>()
            assertThat((state as DashboardViewModel.DashboardScreenState.Content).weeklySummary).isNotNull()
        }
    }

    @Test
    fun `Should only generate weekly summary every week`() = runTest {
        every { dataSource.fetchAll() } returns flowOf(listOf(Diary("Hello World")))
        every { dataSource.getWeeklySummary() } returns WeeklySummary(
            summary = "Old diary summary",
            date = Clock.System.now()
                .minus(value = 5, unit = DateTimeUnit.DAY, TimeZone.UTC),
        )
        every { diaryAI.getWeeklySummary(isAny()) } returns flowOf("New Diary Summary")

        dashboardViewModel.state.test {
            dashboardViewModel.loadDashboardContent()
            // Skip the loading state
            skipItems(2)
            val state = awaitItem() as? DashboardViewModel.DashboardScreenState.Content

            cancelAndIgnoreRemainingEvents()
            assertThat(state).isNotNull()
            assertThat(state?.weeklySummary).isEqualTo("Old diary summary")
        }
    }

    @Test
    fun `Should generate weekly summary when weekly summary is older than a week`() = runTest {
        every { dataSource.fetchAll() } returns flowOf(listOf(Diary("Hello World")))
        every { dataSource.insertWeeklySummary(isAny()) } returns Unit
        every { dataSource.getWeeklySummary() } returns WeeklySummary(
            summary = "Old diary summary",
            date = Clock.System.now()
                .minus(value = 20, unit = DateTimeUnit.DAY, TimeZone.UTC),
        )
        every { diaryAI.getWeeklySummary(isAny()) } returns flowOf("New Diary Summary")

        dashboardViewModel.state.test {
            dashboardViewModel.loadDashboardContent()
            // Skip the loading state
            skipItems(2)
            val state = awaitItem() as? DashboardViewModel.DashboardScreenState.Content

            cancelAndIgnoreRemainingEvents()
            assertThat(state).isNotNull()
            assertThat(state?.weeklySummary).isEqualTo("New Diary Summary")
        }
    }
}