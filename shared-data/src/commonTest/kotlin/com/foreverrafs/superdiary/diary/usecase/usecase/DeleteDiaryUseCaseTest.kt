package com.foreverrafs.superdiary.diary.usecase.usecase

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.doesNotContain
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.usecase.DeleteDiaryUseCase
import com.foreverrafs.superdiary.diary.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.diary.usecase.InMemoryDataSource
import com.foreverrafs.superdiary.diary.usecase.insertRandomDiaries
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class DeleteDiaryUseCaseTest {
    private val dataSource: DataSource = InMemoryDataSource()
    private val getAllDiariesUseCase = GetAllDiariesUseCase(dataSource)
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
}
