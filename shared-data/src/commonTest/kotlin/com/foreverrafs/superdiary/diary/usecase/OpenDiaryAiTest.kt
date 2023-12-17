package com.foreverrafs.superdiary.diary.usecase

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isNotEmpty
import com.aallam.openai.api.chat.ChatChoice
import com.aallam.openai.api.chat.ChatChunk
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatDelta
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.foreverrafs.superdiary.diary.diaryai.OpenDiaryAI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.kodein.mock.Mock
import org.kodein.mock.tests.TestsWithMocks
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OpenDiaryAiTest : TestsWithMocks() {
    override fun setUpMocks() = injectMocks(mocker)

    @Mock
    lateinit var openAI: OpenAI

    private val openDiaryAI by lazy { OpenDiaryAI(openAI) }

    private val chatCompletionChunk = ChatCompletionChunk(
        id = "id",
        created = 1230,
        model = ModelId("asdf"),
        usage = null,
        choices = listOf(
            ChatChunk(0, ChatDelta(content = "summary"), null),
        ),
    )

    private val chatCompletion = ChatCompletion(
        id = "id",
        created = 1200L,
        model = ModelId(""),
        usage = null,
        choices = listOf(
            ChatChoice(
                index = 0,
                message = ChatMessage(role = ChatRole.Assistant, content = "some message response"),
            ),
        ),
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        every { openAI.chatCompletions(isAny()) } returns flowOf(chatCompletionChunk)
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Should return weekly summary`() = runTest {
        openDiaryAI.getWeeklySummary(emptyList()).test {
            val summary = awaitItem()
            awaitComplete()

            assertThat(summary).isNotEmpty()
        }
    }

    @Test
    fun `Should query diary entries`() = runTest {
        everySuspending { openAI.chatCompletion(isAny()) } returns chatCompletion

        val response = openDiaryAI.queryDiaries(emptyList(), emptyList())
        assertThat(response).isNotEmpty()
    }

    @Test
    fun `Should generate diary entry`() = runTest {
        openDiaryAI.generateDiary("hello", 100).test {
            val generated = awaitItem()
            cancelAndIgnoreRemainingEvents()

            assertThat(generated).isNotEmpty()
        }
    }
}
