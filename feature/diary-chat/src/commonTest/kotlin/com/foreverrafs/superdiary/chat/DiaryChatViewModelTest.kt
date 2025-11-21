package com.foreverrafs.superdiary.chat

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.foreverrafs.superdiary.chat.domain.repository.DiaryChatRepository
import com.foreverrafs.superdiary.chat.presentation.DiaryChatViewModel
import com.foreverrafs.superdiary.chat.presentation.DiaryChatViewState
import com.foreverrafs.superdiary.common.coroutines.TestAppDispatchers
import com.foreverrafs.superdiary.common.coroutines.awaitUntil
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.domain.model.Diary
import dev.mokkery.answering.returns
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
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class DiaryChatViewModelTest {

    private val diaryChatRepository: DiaryChatRepository = mock<DiaryChatRepository>()

    private lateinit var diaryChatViewModel: DiaryChatViewModel

    @OptIn(ExperimentalTime::class)
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(TestAppDispatchers.main)

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
            dispatchers = TestAppDispatchers,
        )
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Should include system and context messages right after initialization`() = runTest {
        diaryChatViewModel.viewState.test {
            val state = awaitUntil { it is DiaryChatViewState.Initialized && it.messages.isNotEmpty() }
                as? DiaryChatViewState.Initialized

            assertThat(state?.messages?.size).isEqualTo(2)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Should show an error message when AI returns null`() = runTest {
        everySuspend { diaryChatRepository.queryDiaries(any()) }.returns(null)

        diaryChatViewModel.viewState.test {
            diaryChatViewModel.queryDiaries("tell me")

            val state = awaitUntil {
                it is DiaryChatViewState.Initialized && it.errorText != null
            } as DiaryChatViewState.Initialized

            assertThat(state.isResponding).isEqualTo(false)
            assertThat(state.errorText).isNotNull()

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
            dispatchers = TestAppDispatchers,
        )

        diaryChatViewModel.viewState.test {
            val state =
                awaitUntil { it is DiaryChatViewState.Initialized && it.errorText != null } as DiaryChatViewState.Initialized
            assertThat(state.errorText).isNotNull()
        }
    }
}
