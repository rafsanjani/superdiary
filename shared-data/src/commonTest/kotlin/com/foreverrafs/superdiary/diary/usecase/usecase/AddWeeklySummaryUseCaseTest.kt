package com.foreverrafs.superdiary.diary.usecase.usecase

import assertk.assertThat
import assertk.assertions.isNotNull
import com.foreverrafs.superdiary.diary.Database
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.datasource.LocalDataSource
import com.foreverrafs.superdiary.diary.model.WeeklySummary
import com.foreverrafs.superdiary.diary.usecase.AddWeeklySummaryUseCase
import com.foreverrafs.superdiary.diary.usecase.GetWeeklySummaryUseCase
import com.foreverrafs.superdiary.diary.usecase.datasource.TestDatabaseDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddWeeklySummaryUseCaseTest {
    private val database = Database(TestDatabaseDriver())
    private val dataSource: DataSource = LocalDataSource(database)
    private val addWeeklySummaryUseCase = AddWeeklySummaryUseCase(dataSource)
    private val getWeeklySummaryUseCase = GetWeeklySummaryUseCase(dataSource)

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        database.createDatabase()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Verify that counting all entries returns total added entries`() = runTest {
        addWeeklySummaryUseCase(
            WeeklySummary("Summary", Clock.System.now()),
        )

        val weeklySummary = getWeeklySummaryUseCase()

        assertThat(weeklySummary).isNotNull()
    }
}
