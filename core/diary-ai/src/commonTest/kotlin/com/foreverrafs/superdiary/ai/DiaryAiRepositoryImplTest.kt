package com.foreverrafs.superdiary.ai

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import com.foreverrafs.superdiary.ai.api.DiaryAI
import com.foreverrafs.superdiary.ai.data.DiaryAiRepositoryImpl
import com.foreverrafs.superdiary.database.Database
import com.foreverrafs.superdiary.database.model.DiaryChatMessageDb
import com.foreverrafs.superdiary.database.model.DiaryChatRoleDb
import com.foreverrafs.superdiary.database.testSuperDiaryDatabase
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalTime::class)
class DiaryAiRepositoryImplTest {
    private val diaryAI: DiaryAI = mock {
        every { generateSummary(any(), any()) } returns flowOf()
        every { generateDiary(any(), any()) } returns flowOf()
    }

    private val database: Database = Database(testSuperDiaryDatabase)

    private val diaryAiRepository = DiaryAiRepositoryImpl(
        database = database,
        diaryAI = diaryAI,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Should generate diaries when requested`() = runTest {
        diaryAiRepository.generateDiary(prompt = "prompt", wordCount = 23)

        verifySuspend { diaryAI.generateDiary(prompt = any(), wordCount = any()) }
    }

    @Test
    fun `Should generate summary when requested`() = runTest {
        diaryAiRepository.generateSummary(diaries = emptyList(), onCompletion = {})

        verifySuspend { diaryAI.generateSummary(diaries = emptyList(), any()) }
    }

    @Test
    fun `Clearing chat messages should actually clear it`() = runTest {
        database.saveChatMessage(
            DiaryChatMessageDb(
                id = "123",
                role = DiaryChatRoleDb.DiaryAI,
                timestamp = kotlin.time.Instant.DISTANT_PAST,
                content = "hi",
            ),
        )

        diaryAiRepository.clearChatMessages()

        diaryAiRepository.getChatMessages().test {
            val items = awaitItem()

            assertThat(items).isEmpty()
        }
    }
}
