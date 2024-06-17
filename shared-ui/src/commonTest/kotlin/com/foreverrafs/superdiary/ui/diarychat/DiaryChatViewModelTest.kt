package com.foreverrafs.superdiary.ui.diarychat

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.foreverrafs.superdiary.TestAppDispatchers
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.diaryai.DiaryAI
import com.foreverrafs.superdiary.data.diaryai.DiaryChatRole
import com.foreverrafs.superdiary.data.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.ui.feature.diarychat.DiaryChatViewModel
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
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class DiaryChatViewModelTest {

    private val dataSource: DataSource = mock<DataSource>()

    private val diaryAI: DiaryAI = mock<DiaryAI>()

    private lateinit var diaryChatViewModel: DiaryChatViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())

        every { dataSource.fetchAll() }.returns(flowOf(emptyList()))
        diaryChatViewModel = DiaryChatViewModel(
            diaryAI,
            GetAllDiariesUseCase(dataSource, TestAppDispatchers),
            AggregateLogger(emptyList()),
        )
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Should update responding to true when generating AI response`() = runTest {
        everySuspend { diaryAI.queryDiaries(any()) }.returns("hello boss")

        diaryChatViewModel.state.test {
            diaryChatViewModel.queryDiaries("hello World")

            // Skip the initial state
            skipItems(1)
            val state = awaitItem()

            cancelAndConsumeRemainingEvents()

            assertThat(state.isResponding).isTrue()
            assertThat(state.messages).hasSize(2)
        }
    }

    @Test
    fun `Should update responding to false after generating AI response`() = runTest {
        everySuspend { diaryAI.queryDiaries(any()) }.returns("hello boss")

        diaryChatViewModel.state.test {
            diaryChatViewModel.queryDiaries("hello World")

            // Skip the initial state and first emission state
            skipItems(2)
            val state = awaitItem()

            cancelAndConsumeRemainingEvents()

            assertThat(state.isResponding).isFalse()

            // At least 5 messages will be present after a single query.
            // [Welcome message, system instruction, items, query, response]
            assertThat(state.messages).hasSize(5)
        }
    }

    @Test
    fun `Should prepend message prompt to messages when querying initially`() = runTest {
        everySuspend {
            diaryAI.queryDiaries(
                any(),
            )
        }.returns("You went horse riding on the 20th of June")

        diaryChatViewModel.state.test {
            diaryChatViewModel.queryDiaries("When did I go horse riding?")

            // Skip the initial state and first emission state
            skipItems(2)
            val state = awaitItem()

            cancelAndConsumeRemainingEvents()

            assertThat(state.isResponding).isFalse()
            assertThat(state.messages.any { it.role == DiaryChatRole.System }).isTrue()
        }
    }
}
