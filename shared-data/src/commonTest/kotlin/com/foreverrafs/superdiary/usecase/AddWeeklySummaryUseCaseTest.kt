package com.foreverrafs.superdiary.usecase

import assertk.assertThat
import assertk.assertions.isNotNull
import com.foreverrafs.superdiary.common.coroutines.TestAppDispatchers
import com.foreverrafs.superdiary.data.datasource.LocalDataSource
import com.foreverrafs.superdiary.database.Database
import com.foreverrafs.superdiary.database.testSuperDiaryDatabase
import com.foreverrafs.superdiary.domain.model.WeeklySummary
import com.foreverrafs.superdiary.domain.repository.DataSource
import com.foreverrafs.superdiary.domain.usecase.AddWeeklySummaryUseCase
import com.foreverrafs.superdiary.domain.usecase.GetWeeklySummaryUseCase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock

@OptIn(ExperimentalCoroutinesApi::class)
class AddWeeklySummaryUseCaseTest {
    private val database = Database(testSuperDiaryDatabase)
    private val dataSource: DataSource = LocalDataSource(database)
    private val addWeeklySummaryUseCase = AddWeeklySummaryUseCase(dataSource, TestAppDispatchers)
    private val getWeeklySummaryUseCase = GetWeeklySummaryUseCase(dataSource, TestAppDispatchers)

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
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
