package com.foreverrafs.superdiary.ui.dashboard

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import com.foreverrafs.superdiary.TestAppDispatchers
import com.foreverrafs.superdiary.core.logging.Logger
import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.diaryai.DiaryAI
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.data.model.WeeklySummary
import com.foreverrafs.superdiary.data.usecase.AddWeeklySummaryUseCase
import com.foreverrafs.superdiary.data.usecase.CalculateBestStreakUseCase
import com.foreverrafs.superdiary.data.usecase.CalculateStreakUseCase
import com.foreverrafs.superdiary.data.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.data.usecase.GetWeeklySummaryUseCase
import com.foreverrafs.superdiary.data.usecase.UpdateDiaryUseCase
import com.foreverrafs.superdiary.data.utils.DiaryPreference
import com.foreverrafs.superdiary.data.utils.DiarySettings
import com.foreverrafs.superdiary.ui.feature.dashboard.DashboardViewModel
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {
    private val dataSource: DataSource = mock<DataSource>()

    private lateinit var dashboardViewModel: DashboardViewModel

    private val diaryAI: DiaryAI = mock<DiaryAI>()

    private val diaryPreference: DiaryPreference = mock<DiaryPreference> {
        everySuspend { save(any()) }.returns(Unit)
    }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())

        dashboardViewModel = DashboardViewModel(
            getAllDiariesUseCase = GetAllDiariesUseCase(dataSource, TestAppDispatchers),
            calculateStreakUseCase = CalculateStreakUseCase(TestAppDispatchers),
            addWeeklySummaryUseCase = AddWeeklySummaryUseCase(dataSource, TestAppDispatchers),
            getWeeklySummaryUseCase = GetWeeklySummaryUseCase(dataSource, TestAppDispatchers),
            diaryAI = diaryAI,
            calculateBestStreakUseCase = CalculateBestStreakUseCase(TestAppDispatchers),
            updateDiaryUseCase = UpdateDiaryUseCase(dataSource, TestAppDispatchers),
            logger = Logger,
            preference = diaryPreference,
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
        every { dataSource.fetchAll() }.returns(
            flowOf(
                listOf(Diary("Hello World")),
            ),
        )
        every { dataSource.getWeeklySummary() }.returns(
            WeeklySummary("This is your summary for the week"),
        )

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
        every { dataSource.fetchAll() }.returns(
            flowOf(listOf(Diary("Hello World"))),
        )

        every { dataSource.getWeeklySummary() }.returns(
            WeeklySummary(
                summary = "Old diary summary",
                date = Clock.System.now()
                    .minus(
                        value = 5,
                        unit = DateTimeUnit.DAY,
                        timeZone = TimeZone.UTC,
                    ),
            ),
        )
        every { diaryAI.getWeeklySummary(any()) }.returns(flowOf("New Diary Summary"))

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
        every { dataSource.fetchAll() }.returns(flowOf(listOf(Diary("Hello World"))))
        everySuspend { dataSource.insertWeeklySummary(any()) }.returns(Unit)
        every { dataSource.getWeeklySummary() }.returns(
            WeeklySummary(
                summary = "Old diary summary",
                date = Clock.System.now()
                    .minus(value = 20, unit = DateTimeUnit.DAY, TimeZone.UTC),
            ),
        )
        every { diaryAI.getWeeklySummary(any()) }.returns(flowOf("New Diary Summary"))

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

    @Test
    fun `Should save settings when dashboard ordering is changed`() = runTest {
        dashboardViewModel.updateSettings(DiarySettings.Empty)
        delay(100)
        verifySuspend { diaryPreference.save(any()) }
    }

    @Test
    fun `Should toggle favorite when favorite is toggled`() = runTest {
        val diary = Diary("Hello World")
        every { dataSource.fetchAll() }.returns(flowOf(listOf(diary)))
        everySuspend { dataSource.update(any()) }.returns(1)

        dashboardViewModel.toggleFavorite(diary)

        verifySuspend { dataSource.update(any()) }
    }
}
