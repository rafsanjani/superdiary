package com.foreverrafs.superdiary.usecase

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import com.foreverrafs.superdiary.common.coroutines.TestAppDispatchers
import com.foreverrafs.superdiary.data.datasource.LocalDataSource
import com.foreverrafs.superdiary.database.Database
import com.foreverrafs.superdiary.database.testSuperDiaryDatabase
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.usecase.ClearDiariesUseCase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class ClearDiariesUseCaseTest {
    private val dataSource = LocalDataSource(Database(testSuperDiaryDatabase))

    private val clearDiariesUseCase = ClearDiariesUseCase(dataSource, TestAppDispatchers)

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(TestAppDispatchers.main)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Clearing diaries should delete all diaries`() = runTest {
        // add an entry
        dataSource.add(Diary("Hello World!"))

        // clear all the entries
        clearDiariesUseCase()

        dataSource.fetchAll().test {
            val items = awaitItem()
            assertThat(items).isEmpty()
        }
    }
}
