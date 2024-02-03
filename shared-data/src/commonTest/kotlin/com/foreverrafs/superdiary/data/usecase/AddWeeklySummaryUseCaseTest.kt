package com.foreverrafs.superdiary.data.usecase

import assertk.assertThat
import assertk.assertions.isNotNull
import com.foreverrafs.superdiary.data.Database
import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.datasource.LocalDataSource
import com.foreverrafs.superdiary.data.model.WeeklySummary
import com.foreverrafs.superdiary.data.datasource.TestDatabaseDriver
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
