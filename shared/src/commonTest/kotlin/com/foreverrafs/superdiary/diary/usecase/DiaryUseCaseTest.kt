package com.foreverrafs.superdiary.diary.usecase

import app.cash.turbine.test
import com.foreverrafs.superdiary.diary.Result
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.Diary
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlin.random.Random
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DiaryUseCaseTest {
    private val dataSource: DataSource = LocalStorageDataSource()
    private val addDiaryUseCase = AddDiaryUseCase(dataSource)
    private val getAllDiariesUseCase = GetAllDiariesUseCase(dataSource)
    private val deleteDiaryUseCase = DeleteDiaryUseCase(dataSource)
    private val searchDiaryUseCase = SearchDiaryUseCase(dataSource)

    @BeforeTest
    fun setup() {
        insertRandomDiaries()
    }

    private fun insertRandomDiaries() {
        runBlocking {
            repeat(30) {
                addDiaryUseCase(
                    Diary(
                        Random.nextLong(),
                        entry = "Diary Entry #$it",
                        date = "Monday, Jan 01, 2023",
                    ),
                )
            }
        }
    }

    @Test
    fun `Add new Diary and confirm saved`() = runTest {
        val diary = Diary(
            entry = "New Entry",
            date = "Jan 01, 2023",
        )

        addDiaryUseCase(diary)

        getAllDiariesUseCase.diaries.test {
            val items = awaitItem()
            cancelAndConsumeRemainingEvents()
            assertContains(items, diary)
        }
    }

    @Test
    fun `Delete diary and confirm deletion`() = runTest {
        getAllDiariesUseCase.diaries.test {
            var diaries = expectMostRecentItem()
            val firstDiary = diaries.first()

            // delete the first entry
            this@DiaryUseCaseTest.deleteDiaryUseCase.deleteDiary(firstDiary)

            // get latest diaries again
            diaries = awaitItem()

            // confirm that the first diary has been deleted
            assertFalse { diaries.contains(firstDiary) }
        }
    }

    @Test
    fun `Searching for a valid diary returns it`() = runTest {
        // search for the first diary
        val result = searchDiaryUseCase(query = "Diary Entry #8")

        assertIs<Result.Success>(result)
        assertTrue { result.data.isNotEmpty() }
    }

    @Test
    fun `Searching for an invalid diary returns empty data`() = runTest {
        // search for the first diary
        val result = searchDiaryUseCase(query = "Diary Entry #800")

        assertIs<Result.Success>(result)
        assertTrue { result.data.isEmpty() }
    }

    @Test
    fun `Delete All Diaries Clears Diaries`() = runTest {
        getAllDiariesUseCase.diaries.test {
            val originalDiaryList = expectMostRecentItem()

            assertTrue {
                originalDiaryList.isNotEmpty()
            }

            // clear all the diaries
            deleteDiaryUseCase.deleteAll()

            // fetch all the diaries
            val diaries = awaitItem()
            cancelAndConsumeRemainingEvents()

            // verify all diaries have been cleared
            assertTrue { diaries.isEmpty() }
        }
    }
}
