package com.foreverrafs.superdiary.ai

import ai.koog.prompt.executor.model.PromptExecutor
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
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

@Suppress("UnusedFlow")
@OptIn(ExperimentalCoroutinesApi::class)
class DiaryAiImplTest {

    private val promptExecutor: PromptExecutor = mock(
        mode = MockMode.strict,
    ) {
        everySuspend { models() } returns listOf("funky-maze")
    }
    private val diaryAi = DiaryAiImpl(
        logger = AggregateLogger(emptyList()),
        promptExecutor = promptExecutor,
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        everySuspend {
            promptExecutor.execute(
                prompt = any(),
                model = any(),
                tools = any(),
            )
        } returns listOf()

        everySuspend {
            promptExecutor.executeStreaming(
                prompt = any(),
                model = any(),
                tools = any(),
            )
        } returns flowOf()
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Should execute streaming LLM call when generating summary`() = runTest {
        diaryAi.generateSummary(diaries = emptyList(), onCompletion = {})

        verifySuspend { promptExecutor.executeStreaming(prompt = any(), model = any(), tools = any()) }
    }

    @Test
    fun `Should execute LLM call when querying diaries`() = runTest {
        diaryAi.queryDiaries(emptyList())
        verifySuspend { promptExecutor.execute(prompt = any(), model = any(), tools = any()) }
    }

    @Test
    fun `Should execute streaming LLM call when generating diary`() = runTest {
        diaryAi.generateDiary(prompt = "", wordCount = 100)
        verifySuspend { promptExecutor.executeStreaming(any(), any()) }
    }
}
