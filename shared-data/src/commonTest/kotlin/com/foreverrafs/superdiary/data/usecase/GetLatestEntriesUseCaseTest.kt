package com.foreverrafs.superdiary.data.usecase

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import com.foreverrafs.superdiary.data.Database
import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.datasource.LocalDataSource
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.data.datasource.TestDatabaseDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetLatestEntriesUseCaseTest {
    private val database = Database(TestDatabaseDriver())
    private val dataSource: DataSource = LocalDataSource(database)

    private val addDiaryUseCase = AddDiaryUseCase(dataSource) {
        // no-op validator
    }
    private val getLatestEntriesUseCase = GetLatestEntriesUseCase(dataSource)

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        database.createDatabase()
        database.clearDiaries()
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Last inserted item should be at the top`() = runTest {
        val items = listOf(
            Diary(
                entry = "Latest Diary Entry #1",
                date = Clock.System.now(),
                isFavorite = false,
            ),
            Diary(
                entry = "Latest Diary Entry #2",
                date = Clock.System.now()
                    .plus(10, DateTimeUnit.MINUTE, TimeZone.currentSystemDefault()),
                isFavorite = false,
            ),
        )

        items.forEach { diary ->
            addDiaryUseCase(diary)
        }

        getLatestEntriesUseCase(2).test {
            val diaries = awaitItem()
            cancelAndConsumeRemainingEvents()

            assertThat(diaries).isNotEmpty()
            assertThat(diaries.first().entry).isEqualTo("Latest Diary Entry #2")
        }
    }
}
