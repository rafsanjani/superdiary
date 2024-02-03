package com.foreverrafs.superdiary.data.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.foreverrafs.superdiary.data.Database
import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.datasource.LocalDataSource
import com.foreverrafs.superdiary.data.datasource.TestDatabaseDriver
import com.foreverrafs.superdiary.data.insertRandomDiaries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CountDiariesUseCaseTest {
    private val database = Database(TestDatabaseDriver())
    private val dataSource: DataSource = LocalDataSource(database)
    private val countDiariesUseCase = CountDiariesUseCase(dataSource)

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        database.createDatabase()
        insertRandomDiaries(dataSource, ITEMS_COUNT)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Verify that counting all entries returns total added entries`() = runTest {
        val totalEntries = countDiariesUseCase()

        assertThat(totalEntries).isEqualTo(ITEMS_COUNT.toLong())
    }

    companion object {
        private const val ITEMS_COUNT = 200
    }
}
