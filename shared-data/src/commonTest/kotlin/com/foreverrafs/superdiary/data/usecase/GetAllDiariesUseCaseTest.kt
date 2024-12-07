package com.foreverrafs.superdiary.data.usecase

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.foreverrafs.superdiary.data.Database
import com.foreverrafs.superdiary.data.TestAppDispatchers
import com.foreverrafs.superdiary.data.datasource.LocalDataSource
import com.foreverrafs.superdiary.data.datasource.TestDatabaseDriver
import com.foreverrafs.superdiary.data.insertRandomDiaries
import com.foreverrafs.superdiary.domain.repository.DataSource
import com.foreverrafs.superdiary.domain.usecase.GetAllDiariesUseCase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class GetAllDiariesUseCaseTest {
    private val database = Database(TestDatabaseDriver())
    private val dataSource: DataSource = LocalDataSource(database = database)
    private val getAllDiariesUseCase = GetAllDiariesUseCase(dataSource = dataSource, dispatchers = TestAppDispatchers)

    @BeforeTest
    fun setup() {
        database.createDatabase()
        database.clearDiaries()
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Getting all diaries should return all entries`() = runTest {
        insertRandomDiaries(dataSource, 30)

        getAllDiariesUseCase().test {
            val diaries = awaitItem()

            // We inserted 30 items at the beginning
            assertThat(diaries.size).isEqualTo(30)
        }
    }
}
