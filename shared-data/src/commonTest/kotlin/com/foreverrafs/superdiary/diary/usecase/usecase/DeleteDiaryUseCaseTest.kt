package com.foreverrafs.superdiary.diary.usecase.usecase

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.doesNotContain
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.usecase.DeleteAllDiariesUseCase
import com.foreverrafs.superdiary.diary.usecase.DeleteDiaryUseCase
import com.foreverrafs.superdiary.diary.usecase.DeleteMultipleDiariesUseCase
import com.foreverrafs.superdiary.diary.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.diary.usecase.datasource.InMemoryDataSource
import com.foreverrafs.superdiary.diary.usecase.insertRandomDiaries
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class DeleteDiaryUseCaseTest {
    private val dataSource: DataSource = InMemoryDataSource()
    private val getAllDiariesUseCase = GetAllDiariesUseCase(dataSource)
    private val deleteAllDiariesUseCase = DeleteAllDiariesUseCase(dataSource)
    private val deleteMultipleDiariesUseCase = DeleteMultipleDiariesUseCase(dataSource)
    private val deleteDiaryUseCase = DeleteDiaryUseCase(dataSource)

    @BeforeTest
    fun setup() {
        insertRandomDiaries(dataSource)
    }

    @Test
    fun `Delete diary and confirm deletion`() = runTest {
        getAllDiariesUseCase().test {
            var diaries = expectMostRecentItem().toList()
            val firstDiary = diaries.first()

            // delete the first entry
            deleteDiaryUseCase(firstDiary)

            // get latest diaries again
            diaries = awaitItem().toList()

            cancelAndConsumeRemainingEvents()

            // confirm that the first diary has been deleted
            assertThat(diaries).doesNotContain(firstDiary)
        }
    }

    @Test
    fun `Delete All Diaries Clears Diaries`() = runTest {
        getAllDiariesUseCase().test {
            val originalDiaryList = expectMostRecentItem().toList()

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
    fun `Delete multiple diaries actually deletes them`() = runTest {
        getAllDiariesUseCase().test {
            // Given initial diary items - We need to convert the resulting List to another list again
            // to prevent it from getting overwritten by the subsequent call to awaitItem()
            val originalList = awaitItem().toList()

            // Delete the first two entries
            deleteMultipleDiariesUseCase(originalList.take(2))

            // fetch the remaining diaries
            val currentList = awaitItem()

            cancelAndIgnoreRemainingEvents()
            assertThat(currentList.size).isEqualTo(originalList.size - 2)
        }
    }
}
