package com.foreverrafs.superdiary.ai.usecase

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import com.foreverrafs.superdiary.ai.data.DiaryAiRepositoryImpl
import com.foreverrafs.superdiary.ai.domain.model.DiaryChatMessage
import com.foreverrafs.superdiary.ai.domain.repository.DiaryAiRepository
import com.foreverrafs.superdiary.ai.domain.usecase.GetChatMessagesUseCase
import com.foreverrafs.superdiary.ai.domain.usecase.SaveChatMessageUseCase
import com.foreverrafs.superdiary.database.Database
import com.foreverrafs.superdiary.database.testSuperDiaryDatabase
import dev.mokkery.mock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class SaveChatMessageUseCaseTest {
    private val database = Database(testSuperDiaryDatabase)
    private val repository: DiaryAiRepository = DiaryAiRepositoryImpl(
        database = database,
        diaryAI = mock(),
    )

    private val saveChatMessageUseCase =
        SaveChatMessageUseCase(
            repository = repository,
        )

    private val getChatMessagesUseCase = GetChatMessagesUseCase(
        repository = repository,
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Saving chat message should actually save it`() = runTest {
        getChatMessagesUseCase().test {
            assertThat(awaitItem()).isEmpty()

            saveChatMessageUseCase(
                DiaryChatMessage.System("Hello you are diary AI"),
            )

            assertThat(awaitItem()).hasSize(1)
        }
    }
}
