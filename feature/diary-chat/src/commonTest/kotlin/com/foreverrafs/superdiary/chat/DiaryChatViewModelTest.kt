package com.foreverrafs.superdiary.chat

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.foreverrafs.superdiary.ai.api.DiaryAI
import com.foreverrafs.superdiary.ai.domain.model.DiaryChatRole
import com.foreverrafs.superdiary.chat.domain.repository.DiaryChatRepository
import com.foreverrafs.superdiary.chat.presentation.DiaryChatViewModel
import com.foreverrafs.superdiary.common.coroutines.awaitUntil
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.domain.model.Diary
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class DiaryChatViewModelTest {

    private val diaryChatRepository: DiaryChatRepository = mock<DiaryChatRepository>()

    private val diaryAI: DiaryAI = mock<DiaryAI>()

    private lateinit var diaryChatViewModel: DiaryChatViewModel

    @OptIn(ExperimentalTime::class)
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())

        every { diaryChatRepository.getAllDiaries() }.returns(
            flowOf(
                listOf(
                    Diary(
                        id = 123L,
                        entry = "I am going horse riding today",
                    ),
                ),
            ),
        )
        every { diaryChatRepository.getChatMessages() }.returns(flowOf(emptyList()))
        everySuspend { diaryChatRepository.saveChatMessage(any()) }.returns(Unit)

        diaryChatViewModel = DiaryChatViewModel(
            logger = AggregateLogger(emptyList()),
            repository = diaryChatRepository,
        )
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Should include system and welcome messages right after initialization`() = runTest {
        // No history already configured in setup
        diaryChatViewModel.viewState.test {
            val state = awaitUntil { it.messages.any { m -> m.role == DiaryChatRole.System } }
            // System message should be first
            assertThat(state.messages.first().role).isEqualTo(DiaryChatRole.System)
            // Welcome message from AI should also be present somewhere after system
            assertThat(state.messages.any { it.role == DiaryChatRole.DiaryAI }).isTrue()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Blank query is ignored without changing state`() = runTest {
        // Capture the current messages snapshot
        val initial = diaryChatViewModel.viewState.value
        diaryChatViewModel.queryDiaries("   ")
        advanceUntilIdle()
        val after = diaryChatViewModel.viewState.value

        assertThat(after.isResponding).isFalse()
        assertThat(after.messages.size).isEqualTo(initial.messages.size)
        assertThat(after.error).isNull()
    }

    @Test
    fun `AI returns null sets error and does not append AI message`() = runTest {
        everySuspend { diaryAI.queryDiaries(any()) }.returns(null)

        diaryChatViewModel.viewState.test {
            diaryChatViewModel.queryDiaries("tell me")

            val state = awaitUntil { it.error != null }

            assertThat(state.isResponding).isFalse()

            // The last message should be the user's query since AI failed
            assertThat(state.messages.last().role).isEqualTo(DiaryChatRole.User)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `AI throws exception sets generic error`() = runTest {
        everySuspend { diaryAI.queryDiaries(any()) } throws RuntimeException("boom")

        diaryChatViewModel.viewState.test {
            diaryChatViewModel.queryDiaries("hello")

            val state = awaitUntil { it.error != null }

            assertThat(state.isResponding).isFalse()
            assertThat(state.error).isEqualTo("An error occurred while processing your query")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Diaries failure sets error during initialization`() = runTest {
        // Reconfigure data source to throw from its flow
        every { diaryChatRepository.getAllDiaries() }.returns(flow { throw IllegalStateException("fetch fail") })

        // Recreate VM to re-run init collection with the failing flow
        diaryChatViewModel = DiaryChatViewModel(
            logger = AggregateLogger(emptyList()),
            repository = diaryChatRepository,
        )

        diaryChatViewModel.viewState.test {
            val state = awaitUntil { it.error != null }
            assertThat(state.error).isNotNull()
        }
    }
}
