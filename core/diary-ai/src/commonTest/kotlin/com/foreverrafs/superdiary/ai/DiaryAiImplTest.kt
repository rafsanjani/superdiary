package com.foreverrafs.superdiary.ai

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.domain.model.Diary
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@Suppress("UnusedFlow")
@OptIn(ExperimentalCoroutinesApi::class)
class DiaryAiImplTest {

    private val promptExecutor = FakePromptExecutor()
    private val diaryAi = DiaryAiImpl(
        logger = AggregateLogger(emptyList()),
        promptExecutor = promptExecutor,
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        promptExecutor.reset()
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Should execute streaming LLM call when generating summary`() = runTest {
        diaryAi.generateSummary(diaries = emptyList(), onCompletion = {})

        assertThat(1).isEqualTo(promptExecutor.executeStreamingCalls)
    }

    @Test
    fun `Should execute LLM call when generating writing insights`() = runTest {
        diaryAi.generateWritingInsights(listOf(Diary(entry = "A journal entry")))
        assertThat(1).isEqualTo(promptExecutor.executeCalls)
    }

    @Test
    fun `Should analyse every entry in context safe batches`() = runTest {
        val diaries = List(81) { index -> Diary(entry = "Journal entry $index") }

        diaryAi.generateWritingInsights(diaries)

        assertThat(promptExecutor.executeCalls).isEqualTo(4)
    }

    @Test
    fun `Should execute streaming LLM call when generating diary`() = runTest {
        diaryAi.generateDiary(prompt = "", wordCount = 100)
        assertThat(1).isEqualTo(promptExecutor.executeStreamingCalls)
    }
}
