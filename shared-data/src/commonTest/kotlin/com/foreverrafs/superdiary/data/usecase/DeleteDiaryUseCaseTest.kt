package com.foreverrafs.superdiary.data.usecase

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.doesNotContain
import assertk.assertions.isEqualTo
import com.foreverrafs.superdiary.data.Database
import com.foreverrafs.superdiary.data.TestAppDispatchers
import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.datasource.LocalDataSource
import com.foreverrafs.superdiary.data.datasource.TestDatabaseDriver
import com.foreverrafs.superdiary.data.insertRandomDiaries
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
class DeleteDiaryUseCaseTest {
    private val database = Database(TestDatabaseDriver())
    private val dataSource: DataSource = LocalDataSource(database)

    private val getAllDiariesUseCase = GetAllDiariesUseCase(dataSource, TestAppDispatchers)
    private val deleteMultipleDiariesUseCase = DeleteDiaryUseCase(dataSource, TestAppDispatchers)
    private val deleteDiaryUseCase = DeleteDiaryUseCase(dataSource, TestAppDispatchers)

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        database.createDatabase()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Delete diary and confirm deletion`() = runTest {
        insertRandomDiaries(dataSource)
        getAllDiariesUseCase().test {
            var diaries = awaitItem()
            val firstDiary = diaries.first()

            // delete the first entry
            deleteDiaryUseCase(listOf(firstDiary))

            // get latest diaries again
            diaries = awaitItem()

            cancelAndConsumeRemainingEvents()

            // confirm that the first diary has been deleted
            assertThat(diaries).doesNotContain(firstDiary)
        }
    }

    @Test
    fun `Delete multiple diaries actually deletes them`() = runTest {
        insertRandomDiaries(dataSource)

        getAllDiariesUseCase().test {
            // Given initial diary items
            val originalList = awaitItem()

            // Delete the first two entries
            deleteMultipleDiariesUseCase(originalList.take(2))

            // fetch the remaining diaries
            val currentList = awaitItem()

            cancelAndIgnoreRemainingEvents()
            assertThat(currentList.size).isEqualTo(originalList.size - 2)
        }
    }
}
