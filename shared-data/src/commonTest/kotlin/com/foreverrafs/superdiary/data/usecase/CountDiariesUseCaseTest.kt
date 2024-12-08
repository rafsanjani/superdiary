package com.foreverrafs.superdiary.data.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.foreverrafs.superdiary.data.Database
import com.foreverrafs.superdiary.data.TestAppDispatchers
import com.foreverrafs.superdiary.data.datasource.LocalDataSource
import com.foreverrafs.superdiary.data.datasource.TestDatabaseDriver
import com.foreverrafs.superdiary.data.insertRandomDiaries
import com.foreverrafs.superdiary.domain.repository.DataSource
import com.foreverrafs.superdiary.domain.usecase.CountDiariesUseCase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class CountDiariesUseCaseTest {
    private val database = Database(TestDatabaseDriver())
    private val dataSource: DataSource = LocalDataSource(database)
    private val countDiariesUseCase = CountDiariesUseCase(dataSource = dataSource, dispatchers = TestAppDispatchers)

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(TestAppDispatchers.main)
        database.createDatabase()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Verify that counting all entries returns total added entries`() = runTest {
        insertRandomDiaries(dataSource, 1)
        val totalEntries = countDiariesUseCase()

        assertThat(totalEntries).isEqualTo(1)
    }
}
