package com.foreverrafs.superdiary.insights

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.foreverrafs.superdiary.common.coroutines.TestAppDispatchers
import com.foreverrafs.superdiary.common.coroutines.awaitUntil
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.insights.domain.repository.WritingInsightsRepository
import com.foreverrafs.superdiary.insights.presentation.WritingInsightsViewModel
import com.foreverrafs.superdiary.insights.presentation.WritingInsightsViewState
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class WritingInsightsViewModelTest {
    private val repository = mock<WritingInsightsRepository>()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(TestAppDispatchers.main)
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `generates an insight and writing stats from recent entries`() = runTest {
        every { repository.observeEntries() } returns flowOf(
            listOf(Diary(entry = "Today I wrote a useful journal entry")),
        )
        everySuspend { repository.generateInsights(any()) } returns
            """
            PATTERNS: A useful pattern
            CONSISTENCY: A steady rhythm
            TRY NEXT: Add more detail
            """.trimIndent()

        val viewModel = createViewModel()

        viewModel.viewState.test {
            val state = awaitUntil {
                it is WritingInsightsViewState.Content && it.insights.size == 3
            } as WritingInsightsViewState.Content

            assertThat(state.insights.first().content).isEqualTo("A useful pattern")
            assertThat(state.stats.entriesAnalyzed).isEqualTo(1)
            assertThat(state.stats.totalWords).isEqualTo(7)
            assertThat(state.isGenerating).isEqualTo(false)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `shows an empty state when there are no entries`() = runTest {
        every { repository.observeEntries() } returns flowOf(emptyList())

        val viewModel = createViewModel()

        viewModel.viewState.test {
            assertThat(awaitUntil { it is WritingInsightsViewState.Empty })
                .isEqualTo(WritingInsightsViewState.Empty)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createViewModel() = WritingInsightsViewModel(
        logger = AggregateLogger(emptyList()),
        repository = repository,
        dispatchers = TestAppDispatchers,
    )
}
