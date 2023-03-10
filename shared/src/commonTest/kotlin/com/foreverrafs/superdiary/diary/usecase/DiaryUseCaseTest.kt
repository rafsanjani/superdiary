package com.foreverrafs.superdiary.diary.usecase

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.doesNotContain
import assertk.assertions.isEmpty
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotEmpty
import assertk.assertions.messageContains
import com.foreverrafs.superdiary.diary.Result
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.Diary
import kotlin.random.Random
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus

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
            val relaxedAddDiaryUseCase = RelaxedAddDiaryUseCase(dataSource)

            // March 03, 2023
            var currentDate = Instant.parse(isoString = "2023-03-03T02:35:53.049Z")
            repeat(30) {
                relaxedAddDiaryUseCase(
                    Diary(
                        Random.nextLong(),
                        entry = "Diary Entry #$it",
                        date = currentDate.toString(),
                    ),
                )

                currentDate = currentDate.plus(1, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
            }
        }
    }

    @Test
    fun `Add new relaxed diary and confirm saved`() = runTest {
        val relaxedAddDiaryUseCase = RelaxedAddDiaryUseCase(dataSource)

        val diary = Diary(
            entry = "New Entry",
            date = "2023-03-03T03:33:25.587Z",
        )

        relaxedAddDiaryUseCase(diary)

        getAllDiariesUseCase.diaries.test {
            val items = awaitItem()
            cancelAndConsumeRemainingEvents()

            assertThat(items).contains(diary)
        }
    }

    @Test
    fun `Add new diary today and confirm saved`() = runTest {
        val diary = Diary(
            entry = "New Entry",
            date = Clock.System.now().toString(),
        )

        addDiaryUseCase(diary)

        getAllDiariesUseCase.diaries.test {
            val items = awaitItem()
            cancelAndConsumeRemainingEvents()

            assertThat(items).contains(diary)
        }
    }

    @Test
    fun `Adding a new diary in the future fails`() = runTest {
        val today = Clock.System.now()

        val diary = Diary(
            entry = "New Entry",
            date = today.plus(1, DateTimeUnit.DAY, TimeZone.currentSystemDefault()).toString(),
        )

        val result = addDiaryUseCase(diary)

        assertThat(result).isInstanceOf(Result.Failure::class)
    }

    @Test
    fun `Adding a new diary in the past fails`() = runTest {
        val today = Clock.System.now()

        val diary = Diary(
            entry = "New Entry",
            date = today.minus(1, DateTimeUnit.DAY, TimeZone.currentSystemDefault()).toString(),
        )

        val result = addDiaryUseCase(diary)

        assertThat(result).isInstanceOf(Result.Failure::class)
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
            assertThat(diaries).doesNotContain(firstDiary)
        }
    }

    @Test
    fun `Searching for a valid diary returns it`() = runTest {
        // search for the first diary
        val result = searchDiaryUseCase.searchByEntry(entry = "Diary Entry #8")

        assertThat(result).isNotEmpty()
    }

    @Test
    fun `Searching for an invalid diary returns empty data`() = runTest {
        // search for the first diary
        val result = searchDiaryUseCase.searchByEntry(entry = "Diary Entry #800")

        assertThat(result).isEmpty()
    }

    @Test
    fun `Searching for diaries between valid dates with entries returns entries`() = runTest {
        val fromDate = LocalDate.parse("2023-03-20")
        val toDate = LocalDate.parse("2023-03-22")

        val diaries = searchDiaryUseCase.searchBetween(fromDate, toDate)

        assertThat(diaries).isNotEmpty()
    }

    @Test
    fun `Searching for diaries between valid dates without entries returns empty`() = runTest {
        val fromDate = LocalDate.parse("2023-04-04")
        val toDate = LocalDate.parse("2023-04-05")

        val diaries = searchDiaryUseCase.searchBetween(fromDate, toDate)

        assertThat(diaries).isEmpty()
    }

    @Test
    fun `Searching for diaries between invalid dates throw exception`() = runTest {
        val fromDate = LocalDate.parse("2023-03-22")
        val toDate = LocalDate.parse("2023-03-20")

        assertThat { searchDiaryUseCase.searchBetween(fromDate, toDate) }
            .isFailure()
            .messageContains("should be less than or equal to")
    }

    @Test
    fun `Searching for diary for a valid date returns entries`() = runTest {
        val date = LocalDate.parse("2023-03-15")
        val diaries = searchDiaryUseCase.searchByDate(date = date)

        assertThat(diaries).isNotEmpty()
    }

    @Test
    fun `Delete All Diaries Clears Diaries`() = runTest {
        getAllDiariesUseCase.diaries.test {
            val originalDiaryList = expectMostRecentItem()

            assertThat(originalDiaryList).isNotEmpty()

            // clear all the diaries
            deleteDiaryUseCase.deleteAll()

            // fetch all the diaries
            val diaries = awaitItem()
            cancelAndConsumeRemainingEvents()

            // verify all diaries have been cleared
            assertThat(diaries).isEmpty()
        }
    }
}
