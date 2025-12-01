package com.foreverrafs.superdiary.dashboard

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.isEqualTo
import com.foreverrafs.superdiary.ai.api.DiaryAI
import com.foreverrafs.superdiary.common.coroutines.TestAppDispatchers
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.dashboard.domain.GenerateWeeklySummaryUseCase
import com.foreverrafs.superdiary.database.Database
import com.foreverrafs.superdiary.database.model.WeeklySummaryDb
import com.foreverrafs.superdiary.database.testSuperDiaryDatabase
import com.foreverrafs.superdiary.domain.model.Diary
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalTime::class)
class GenerateWeeklySummaryUseCaseTest {

    private val diaryAI: DiaryAI = mock()

    private val testClock: Clock = object : Clock {
        override fun now(): Instant = Instant.parse("2023-05-01T01:01:01.049Z")
    }

    private val diaries: List<Diary> = listOf(
        Diary(
            entry = "Entry 1",
            date = testClock.now(),
        ),
        Diary(
            entry = "Entry 2",
            date = testClock.now(),
        ),
        Diary(
            entry = "Entry 3",
            date = testClock.now(),
        ),
    )

    private lateinit var database: Database

    private lateinit var generateWeeklySummaryUseCase: GenerateWeeklySummaryUseCase

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(TestAppDispatchers.main)
        database = Database(testSuperDiaryDatabase)

        generateWeeklySummaryUseCase = GenerateWeeklySummaryUseCase(
            logger = AggregateLogger(emptyList()),
            database = database,
            diaryAI = diaryAI,
            clock = testClock,
        )
    }

    @Test
    fun `when previous summary is recent, it returns previous summary and skips AI`() = runTest {
        // Insert a summary into the database today
        database.insertWeeklySummary(
            WeeklySummaryDb(
                summary = "Test Summary",
                date = testClock.now(),
            ),
        )

        generateWeeklySummaryUseCase(diaries).test {
            val item = awaitItem()

            assertThat(item).isEqualTo("Test Summary")

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Should generate new summary from AI when old one is older than 7 days`() = runTest {
        every { diaryAI.generateSummary(any(), any()) } returns flowOf("New Test Summary")

        // Insert a summary into the database 10 days ago
        database.insertWeeklySummary(
            WeeklySummaryDb(
                summary = "Old Summary",
                date = testClock.now() - 10.days,
            ),
        )

        generateWeeklySummaryUseCase(diaries).test {
            verifySuspend { diaryAI.generateSummary(any(), any()) }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when AI throws error, propagate the error by emitting it`() = runTest {
        every { diaryAI.generateSummary(any(), any()) } throws Exception("AI Error")

        // Insert a summary into the database 10 days ago, should trigger AI
        database.insertWeeklySummary(
            WeeklySummaryDb(
                summary = "Old Summary",
                date = testClock.now() - 10.days,
            ),
        )

        generateWeeklySummaryUseCase(diaries).test {
            val item = awaitError()

            assertThat(item).hasMessage("AI Error")
            cancelAndIgnoreRemainingEvents()
        }
    }
}
