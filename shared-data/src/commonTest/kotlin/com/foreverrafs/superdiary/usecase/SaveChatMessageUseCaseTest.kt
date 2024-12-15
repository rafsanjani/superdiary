package com.foreverrafs.superdiary.usecase

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import com.foreverrafs.superdiary.data.datasource.LocalDataSource
import com.foreverrafs.superdiary.data.diaryai.DiaryChatMessage
import com.foreverrafs.superdiary.database.Database
import com.foreverrafs.superdiary.database.TestDatabaseDriver
import com.foreverrafs.superdiary.domain.repository.DataSource
import com.foreverrafs.superdiary.domain.usecase.GetChatMessagesUseCase
import com.foreverrafs.superdiary.domain.usecase.SaveChatMessageUseCase
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
    private val database = Database(TestDatabaseDriver())
    private val dataSource: DataSource = LocalDataSource(database)

    private val saveChatMessageUseCase =
        SaveChatMessageUseCase(
            dataSource = dataSource,
        )

    private val getChatMessagesUseCase = GetChatMessagesUseCase(
        dataSource = dataSource,
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        database.createDatabase()
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
