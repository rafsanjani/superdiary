package com.foreverrafs.superdiary.ui.feature.dashboard

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import com.foreverrafs.superdiary.TestDataSource
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.diaryai.DiaryAI
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.usecase.AddDiaryUseCase
import com.foreverrafs.superdiary.diary.usecase.AddWeeklySummaryUseCase
import com.foreverrafs.superdiary.diary.usecase.CalculateStreakUseCase
import com.foreverrafs.superdiary.diary.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.diary.usecase.GetWeeklySummaryUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardScreenModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private val dataSource: DataSource = TestDataSource()
    private val getAllDiariesUseCase = GetAllDiariesUseCase(dataSource)
    private val calculateStreakUseCase = CalculateStreakUseCase()

    private lateinit var dashboardScreenModel: DashboardScreenModel

    private val diaryAI: DiaryAI = object : DiaryAI {
        override fun generateDiary(prompt: String, wordCount: Int): Flow<String> {
            TODO("Not yet implemented")
        }

        override suspend fun generateWeeklySummary(diaries: List<Diary>): String {
            TODO("Not yet implemented")
        }

        override fun generateWeeklySummaryAsync(diaries: List<Diary>): Flow<String> {
            return flowOf("Welcome to your weekly summary!")
        }

        override suspend fun queryDiaries(diaries: List<Diary>, query: String): String {
            TODO("Not yet implemented")
        }
    }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        dashboardScreenModel = DashboardScreenModel(
            getAllDiariesUseCase = getAllDiariesUseCase,
            calculateStreakUseCase = calculateStreakUseCase,
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
    fun `Verify dashboard starts from loading state`() = runTest {
        dashboardScreenModel.state.test {
            val state = awaitItem()

            assertThat(state).isInstanceOf<DashboardScreenModel.DashboardScreenState.Loading>()
        }
    }

    @Test
    fun `Verify weekly summary gets produced if diaries isn't empty`() = runTest {
        val addDiary = AddDiaryUseCase(dataSource) {}

        // Add an entry
        addDiary.invoke(
            Diary(
                entry = "New Entry",
                date = Clock.System.now(),
                isFavorite = false,
            ),
        )

        dashboardScreenModel.state.test {
            dashboardScreenModel.loadDashboardContent()
            // Skip the loading state and the initial success state
            skipItems(2)

            val state = awaitItem()
            cancelAndIgnoreRemainingEvents()

            assertThat(state).isInstanceOf<DashboardScreenModel.DashboardScreenState.Content>()
            assertThat((state as DashboardScreenModel.DashboardScreenState.Content).weeklySummary).isNotNull()
        }
    }

    @Test
    fun `Verify weekly summary is only generated weekly`() = runTest {
        // TODO: Fix this test case
    }

    @Test
    fun `Verify weekly summary isn't generated when diaries is empty`() {
        // TODO: Fix this test case
    }
}
