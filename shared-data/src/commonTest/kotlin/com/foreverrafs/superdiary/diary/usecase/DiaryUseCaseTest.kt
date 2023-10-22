package com.foreverrafs.superdiary.diary.usecase

import app.cash.turbine.test
import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.doesNotContain
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotEmpty
import assertk.assertions.isTrue
import assertk.assertions.messageContains
import com.foreverrafs.superdiary.diary.Result
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.utils.DiaryValidator
import com.foreverrafs.superdiary.diary.utils.toInstant
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlin.random.Random
import kotlin.test.BeforeTest
import kotlin.test.Test

class DiaryUseCaseTest {

    private val dataSource: DataSource = InMemoryDataSource()
    private val validator: DiaryValidator = DiaryValidator(Clock.System)

    private val addDiaryUseCase = AddDiaryUseCase(dataSource, validator)
    private val getAllDiariesUseCase = GetAllDiariesUseCase(dataSource)
    private val deleteDiaryUseCase = DeleteDiaryUseCase(dataSource)
    private val deleteAllDiariesUseCase = DeleteAllDiariesUseCase(dataSource)
    private val searchDiaryBetweenDatesUseCase = SearchDiaryBetweenDatesUseCase(dataSource)
    private val searchDiaryByDateUseCase = SearchDiaryByDateUseCase(dataSource)
    private val searchDiaryByEntryUseCase = SearchDiaryByEntryUseCase(dataSource)
    private val deleteMultipleDiariesUseCase = DeleteMultipleDiariesUseCase(dataSource)
    private val updateDiaryUseCase = UpdateDiaryUseCase(dataSource)

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
                        date = currentDate,
                        isFavorite = false,
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
            date = Instant.parse("2023-03-03T03:33:25.587Z"),
            isFavorite = false,
        )

        relaxedAddDiaryUseCase(diary)

        getAllDiariesUseCase().test {
            val items = awaitItem()
            cancelAndConsumeRemainingEvents()

            assertThat(items).contains(diary)
        }
    }

    @Test
    fun `Add new diary today and confirm saved`() = runTest {
        val diary = Diary(
            id = Random.nextLong(),
            entry = "New Entry today",
            date = Clock.System.now(),
            isFavorite = false,
        )

        addDiaryUseCase(diary)

        getAllDiariesUseCase().test {
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
            date = today.plus(1, DateTimeUnit.DAY, TimeZone.currentSystemDefault()),
            isFavorite = false,
        )

        val result = addDiaryUseCase(diary)

        assertThat(result).isInstanceOf(Result.Failure::class)
    }

    @Test
    fun `Adding a new diary in the past fails`() = runTest {
        val today = Clock.System.now()

        val diary = Diary(
            entry = "New Entry",
            date = today.minus(1, DateTimeUnit.DAY, TimeZone.currentSystemDefault()),
            isFavorite = false,
        )

        val result = addDiaryUseCase(diary)

        assertThat(result).isInstanceOf(Result.Failure::class)
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
    fun `Searching for a valid diary returns it`() = runTest {
        // search for the first diary
        searchDiaryByEntryUseCase(entry = "Entry #8").test {
            val result = expectMostRecentItem()

            assertThat(result).isNotEmpty()
        }
    }

    @Test
    fun `Searching for an invalid diary returns empty data`() = runTest {
        // search for the first diary
        searchDiaryByEntryUseCase(entry = "Diary Entry #800").test {
            val result = expectMostRecentItem()
            assertThat(result).isEmpty()
        }
    }

    @Test
    fun `Searching for diaries between valid dates with entries returns entries`() = runTest {
        val fromDate = LocalDate.parse("2023-03-20")
        val toDate = LocalDate.parse("2023-03-22")

        searchDiaryBetweenDatesUseCase(
            from = fromDate.toInstant(),
            to = toDate.toInstant(),
        ).test {
            val diaries = expectMostRecentItem()
            assertThat(diaries).isNotEmpty()
        }
    }

    @Test
    fun `Searching for diaries between valid dates without entries returns empty`() = runTest {
        val fromDate = LocalDate.parse("2023-04-04")
        val toDate = LocalDate.parse("2023-04-05")

        searchDiaryBetweenDatesUseCase(
            from = fromDate.toInstant(),
            to = toDate.toInstant(),
        ).test {
            val diaries = expectMostRecentItem()
            assertThat(diaries).isEmpty()
        }
    }

    @Test
    fun `Searching for diaries between invalid dates throw exception`() = runTest {
        val fromDate = LocalDate.parse("2023-03-22")
        val toDate = LocalDate.parse("2023-03-20")

        assertFailure {
            searchDiaryBetweenDatesUseCase(fromDate.toInstant(), toDate.toInstant())
        }
            .messageContains("should be less than or equal to")
    }

    @Test
    fun `Searching for diary for a valid date returns entries`() = runTest {
        val date = LocalDate.parse("2023-03-15")

        searchDiaryByDateUseCase(date = date.toInstant()).test {
            val diaries = expectMostRecentItem()
            assertThat(diaries).isNotEmpty()
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
            assertThat(currentList.size).isEqualTo(originalList.size - 2)
        }
    }

    @Test
    fun `Update valid diary entry returns 1 updated row`() = runTest {
        getAllDiariesUseCase().test {
            val originalList = awaitItem()

            var firstEntry = originalList.first()

            // verify that it isnt' favorited
            assertThat(firstEntry.isFavorite).isFalse()

            val updated = updateDiaryUseCase(
                firstEntry.copy(
                    isFavorite = true,
                ),
            )

            // fetch the remaining diaries
            val currentList = awaitItem()

            firstEntry = currentList.first()

            // verify that it has been updated and changed to favorite = true
            assertThat(firstEntry.isFavorite).isTrue()
            assertThat(updated).isTrue()
        }
    }
}
