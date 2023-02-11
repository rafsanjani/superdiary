package com.foreverrafs.superdiary.diary.usecase

import com.foreverrafs.superdiary.diary.Result
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.Diary
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
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
    private val deleteAllDiariesUseCase = DeleteAllDiariesUseCase(dataSource)
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
                        null,
                        entry = "Diary Entry #$it",
                        date = "Monday, Jan 01, 2023"
                    )
                )
            }
        }
    }

    @Test
    fun testAddNewDiary() = runTest {
        val diary = Diary(
            entry = "New Entry",
            date = "Jan 01, 2023"
        )
        val diaries = (getAllDiariesUseCase() as Result.Success).data
        val result = addDiaryUseCase(diary)

        assertIs<Result.Success>(result)
        assertContains(diaries, diary)
    }

    @Test
    fun testDeleteDiary() = runTest {
        // fetch all diaries
        var diaries = (getAllDiariesUseCase() as Result.Success).data
        val firstDiary = diaries.first()

        // delete the first entry
        val result = deleteDiaryUseCase(firstDiary)

        // fetch the diaries again
        diaries = (getAllDiariesUseCase() as Result.Success).data

        // confirm that the first diary has been removed from the list
        assertIs<Result.Success>(result)
        assertFalse { diaries.contains(firstDiary) }
    }

    @Test
    fun testSearchDiary() = runTest {
        // search for the first diary
        val result = searchDiaryUseCase(query = "Diary Entry #8")

        assertIs<Result.Success>(result)
    }

    @Test
    fun testDeleteAllDiaries() = runTest {
        // clear all the diaries
        deleteAllDiariesUseCase.invoke()

        // fetch all the diaries
        val diaries = (getAllDiariesUseCase() as Result.Success).data

        // verify that the list is empty
        assertTrue { diaries.isEmpty() }
    }
}
