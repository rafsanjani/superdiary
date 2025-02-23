package com.foreverrafs.superdiary.ai

import com.foreverrafs.superdiary.ai.api.DiaryAI
import com.foreverrafs.superdiary.ai.data.DiaryAiRepositoryImpl
import com.foreverrafs.superdiary.database.Database
import com.foreverrafs.superdiary.database.testSuperDiaryDatabase
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlin.test.Test
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class DiaryAiRepositoryImplTest {
    private val diaryAI: DiaryAI = mock {
        every { generateSummary(any()) } returns flowOf()
        every { generateDiary(any(), any()) } returns flowOf()
    }

    private val diaryAiRepository = DiaryAiRepositoryImpl(
        database = Database(testSuperDiaryDatabase),
        diaryAI = diaryAI,
    )

    @Test
    fun `Should generate diaries when requested`() = runTest {
        diaryAiRepository.generateDiary(prompt = "prompt", wordCount = 23)

        verifySuspend { diaryAI.generateDiary(prompt = any(), wordCount = any()) }
    }

    @Test
    fun `Should generate summary when requested`() = runTest {
        diaryAiRepository.generateSummary(diaries = emptyList())

        verifySuspend { diaryAI.generateSummary(diaries = emptyList()) }
    }
}
