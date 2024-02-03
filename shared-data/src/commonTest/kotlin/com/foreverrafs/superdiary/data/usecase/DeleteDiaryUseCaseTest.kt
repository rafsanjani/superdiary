package com.foreverrafs.superdiary.data.usecase

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.doesNotContain
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import com.foreverrafs.superdiary.data.Database
import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.datasource.LocalDataSource
import com.foreverrafs.superdiary.data.datasource.TestDatabaseDriver
import com.foreverrafs.superdiary.data.insertRandomDiaries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DeleteDiaryUseCaseTest {
    private val database = Database(TestDatabaseDriver())
    private val dataSource: DataSource = LocalDataSource(database)

    private val getAllDiariesUseCase = GetAllDiariesUseCase(dataSource)
    private val deleteAllDiariesUseCase = DeleteAllDiariesUseCase(dataSource)
    private val deleteMultipleDiariesUseCase = DeleteDiaryUseCase(dataSource)
    private val deleteDiaryUseCase = DeleteDiaryUseCase(dataSource)

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        database.createDatabase()
        insertRandomDiaries(dataSource)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Delete diary and confirm deletion`() =
        runTest {
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
    fun `Delete All Diaries Clears Diaries`() =
        runTest {
            getAllDiariesUseCase().test {
                val originalDiaryList = awaitItem()

                assertThat(originalDiaryList).isNotEmpty()

                // clear all the diaries
                deleteAllDiariesUseCase()

                // fetch all the diaries
                val diaries = awaitItem()
                cancelAndConsumeRemainingEvents()

                // verify all diaries have been cleared
                assertThat(diaries).isEmpty()
            }
        }

    @Test
    fun `Delete multiple diaries actually deletes them`() =
        runTest {
            getAllDiariesUseCase().test {
                // Given initial diary items - We need to convert the resulting List to another list again
                // to prevent it from getting overwritten by the subsequent call to awaitItem()
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
