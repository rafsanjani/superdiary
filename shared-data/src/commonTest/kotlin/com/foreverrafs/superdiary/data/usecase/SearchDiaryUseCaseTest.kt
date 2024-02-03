package com.foreverrafs.superdiary.data.usecase

import app.cash.turbine.test
import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.messageContains
import com.foreverrafs.superdiary.data.Database
import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.datasource.LocalDataSource
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.data.datasource.TestDatabaseDriver
import com.foreverrafs.superdiary.data.insertRandomDiaries
import com.foreverrafs.superdiary.data.utils.toInstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchDiaryUseCaseTest {

    private val database = Database(TestDatabaseDriver())
    private val dataSource: DataSource = LocalDataSource(database)

    private val searchDiaryBetweenDatesUseCase = SearchDiaryBetweenDatesUseCase(dataSource)
    private val searchDiaryByDateUseCase = SearchDiaryByDateUseCase(dataSource)
    private val searchDiaryByEntryUseCase = SearchDiaryByEntryUseCase(dataSource)

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        database.createDatabase()
        database.clearDiaries()
        insertRandomDiaries(dataSource)
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Searching for a valid diary returns it`() = runTest {
        // search for the first diary
        searchDiaryByEntryUseCase(entry = "Entry #8").test {
            val result = awaitItem()
            assertThat(result).isNotEmpty()
        }
    }

    @Test
    fun `Searching for an invalid diary returns empty data`() = runTest {
        // search for the first diary
        searchDiaryByEntryUseCase(entry = "Diary Entry #800").test {
            val result = awaitItem()
            assertThat(result).isEmpty()
        }
    }

    @Test
    fun `Searching for diaries between valid dates with entries returns entries`() = runTest {
        val fromDate = LocalDate.parse("2023-03-03")
        val toDate = LocalDate.parse("2023-03-10")

        searchDiaryBetweenDatesUseCase(
            from = fromDate.toInstant(),
            to = toDate.toInstant(),
        ).test {
            val diaries = awaitItem()
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
            val diaries = awaitItem()
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
        val relaxedAddDiaryUseCase = AddDiaryUseCase(dataSource) {}
        val date = LocalDate.parse("2023-03-03")

        relaxedAddDiaryUseCase(Diary(entry = "", date = date.toInstant(), isFavorite = false))

        searchDiaryByDateUseCase(date = date.toInstant()).test {
            val diaries = awaitItem()

            assertThat(diaries).isNotEmpty()
            assertThat(diaries.size).isEqualTo(2)
        }
    }
}
