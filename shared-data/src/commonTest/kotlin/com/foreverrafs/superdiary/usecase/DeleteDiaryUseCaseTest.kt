package com.foreverrafs.superdiary.usecase

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.doesNotContain
import assertk.assertions.isEqualTo
import com.foreverrafs.superdiary.TestAppDispatchers
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.data.datasource.LocalDataSource
import com.foreverrafs.superdiary.database.Database
import com.foreverrafs.superdiary.database.testSuperDiaryDatabase
import com.foreverrafs.superdiary.domain.repository.DataSource
import com.foreverrafs.superdiary.domain.usecase.DeleteDiaryUseCase
import com.foreverrafs.superdiary.domain.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.insertRandomDiaries
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
    private val database = Database(testSuperDiaryDatabase)
    private val dataSource: DataSource = LocalDataSource(database)

    private val getAllDiariesUseCase = GetAllDiariesUseCase(dataSource, TestAppDispatchers)
    private val deleteMultipleDiariesUseCase = DeleteDiaryUseCase(dataSource, TestAppDispatchers)
    private val deleteDiaryUseCase = DeleteDiaryUseCase(dataSource, TestAppDispatchers)

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Delete diary and confirm deletion`() = runTest {
        insertRandomDiaries(dataSource)
        getAllDiariesUseCase().test {
            var diaries = (awaitItem() as Result.Success).data
            val firstDiary = diaries.first()

            // delete the first entry
            deleteDiaryUseCase(listOf(firstDiary))

            // get latest diaries again
            diaries = (awaitItem() as Result.Success).data

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
            val originalList = (awaitItem() as Result.Success).data

            // Delete the first two entries
            deleteMultipleDiariesUseCase(originalList.take(2))

            // fetch the remaining diaries
            val currentList = (awaitItem() as Result.Success).data

            cancelAndIgnoreRemainingEvents()
            assertThat(currentList.size).isEqualTo(originalList.size - 2)
        }
    }
}
