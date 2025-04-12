package com.foreverrafs.superdiary.ai

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import com.aallam.openai.api.chat.ChatChoice
import com.aallam.openai.api.chat.ChatChunk
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatDelta
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.foreverrafs.superdiary.core.logging.AggregateLogger
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
class OpenDiaryAiTest {
    private val openAI: OpenAI = mock<OpenAI>()

    private val openDiaryAI = DiaryAiImpl(
        openAI = openAI,
        logger = AggregateLogger(emptyList()),
    )

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
        every { openAI.chatCompletions(any(), any()) }.returns(flowOf(chatCompletionChunk))
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Should return weekly summary`() = runTest {
        every { openAI.chatCompletions(any(), any()) }.returns(flowOf(chatCompletionChunk))

        openDiaryAI.generateSummary(emptyList()).test {
            val summary = awaitItem()
            awaitComplete()

            assertThat(summary).isNotEmpty()
        }
    }

    @Test
    fun `Should query diary entries`() = runTest {
        everySuspend { openAI.chatCompletion(any(), any()) }.returns(chatCompletion)

        val response = openDiaryAI.queryDiaries(emptyList())
        assertThat(response).isNotNull()
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
