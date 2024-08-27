package com.foreverrafs.superdiary.ui.diarychat

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.foreverrafs.superdiary.TestAppDispatchers
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.diaryai.DiaryAI
import com.foreverrafs.superdiary.data.diaryai.DiaryChatRole
import com.foreverrafs.superdiary.data.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.data.usecase.GetChatMessagesUseCase
import com.foreverrafs.superdiary.data.usecase.SaveChatMessageUseCase
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
        every { dataSource.getChatMessages() }.returns(flowOf(emptyList()))
        everySuspend { dataSource.saveChatMessage(any()) }.returns(Unit)

        diaryChatViewModel = DiaryChatViewModel(
            diaryAI = diaryAI,
            getAllDiariesUseCase = GetAllDiariesUseCase(dataSource, TestAppDispatchers),
            logger = AggregateLogger(emptyList()),
            saveChatMessageUseCase = SaveChatMessageUseCase(dataSource, TestAppDispatchers),
            getChatMessagesUseCase = GetChatMessagesUseCase(dataSource),
        )
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Should update responding to true when generating AI response`() = runTest {
        everySuspend { diaryAI.queryDiaries(any()) }.returns("hello boss")

        diaryChatViewModel.queryDiaries("hello World")

        diaryChatViewModel.state.test {
            skipItems(1)

            val state = awaitItem()

            cancelAndIgnoreRemainingEvents()

            assertThat(state.isResponding).isTrue()
        }
    }

    @Test
    fun `Should update responding to false after generating AI response`() = runTest {
        everySuspend { diaryAI.queryDiaries(any()) }.returns("hello boss")

        diaryChatViewModel.init()

        diaryChatViewModel.state.test {
            diaryChatViewModel.queryDiaries("hello World")

            val state =
                skipWhile { state -> state.messages.none { it.role == DiaryChatRole.DiaryAI } }.awaitItem()

            cancelAndConsumeRemainingEvents()

            assertThat(state.isResponding).isFalse()
        }
    }

    @Test
    fun `Should prepend message prompt to messages when querying initially`() = runTest {
        everySuspend {
            diaryAI.queryDiaries(
                any(),
            )
        }.returns("You went horse riding on the 20th of June")

        everySuspend {
            dataSource.saveChatMessage(any())
        }.returns(Unit)

        diaryChatViewModel.init()

        diaryChatViewModel.queryDiaries("When did I go horse riding?")

        diaryChatViewModel.state.test {
            awaitItem()

            // Skip emissions while AI responses are getting generated
            val state = skipWhile {
                it.isResponding
            }.expectMostRecentItem()

            cancelAndConsumeRemainingEvents()

            assertThat(state.messages.any { it.role == DiaryChatRole.System }).isTrue()
        }
    }

    private suspend inline fun <T> ReceiveTurbine<T>.skipWhile(
        predicate: (value: T) -> Boolean,
    ): ReceiveTurbine<T> {
        while (true) {
            val item = awaitItem()
            if (!predicate(item)) {
                break
            }
        }
        return this
    }
}
