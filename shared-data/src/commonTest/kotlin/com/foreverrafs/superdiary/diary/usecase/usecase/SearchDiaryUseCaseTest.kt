package com.foreverrafs.superdiary.diary.usecase.usecase

import app.cash.turbine.test
import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isNotEmpty
import assertk.assertions.messageContains
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.usecase.SearchDiaryBetweenDatesUseCase
import com.foreverrafs.superdiary.diary.usecase.SearchDiaryByDateUseCase
import com.foreverrafs.superdiary.diary.usecase.SearchDiaryByEntryUseCase
import com.foreverrafs.superdiary.diary.usecase.datasource.InMemoryDataSource
import com.foreverrafs.superdiary.diary.usecase.insertRandomDiaries
import com.foreverrafs.superdiary.diary.utils.toInstant
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.BeforeTest
import kotlin.test.Test

class SearchDiaryUseCaseTest {

    private val dataSource: DataSource = InMemoryDataSource()

    private val searchDiaryBetweenDatesUseCase = SearchDiaryBetweenDatesUseCase(dataSource)
    private val searchDiaryByDateUseCase = SearchDiaryByDateUseCase(dataSource)
    private val searchDiaryByEntryUseCase = SearchDiaryByEntryUseCase(dataSource)

    @BeforeTest
    fun setup() {
        insertRandomDiaries(dataSource)
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
}
