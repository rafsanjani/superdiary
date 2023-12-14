package com.foreverrafs.superdiary.diary.usecase.usecase

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.foreverrafs.superdiary.diary.Database
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.datasource.LocalDataSource
import com.foreverrafs.superdiary.diary.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.diary.usecase.UpdateDiaryUseCase
import com.foreverrafs.superdiary.diary.usecase.datasource.TestDatabaseDriver
import com.foreverrafs.superdiary.diary.usecase.insertRandomDiaries
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
class UpdateDiaryUseCaseTest {
    private val database = Database(TestDatabaseDriver())
    private val dataSource: DataSource = LocalDataSource(database)

    private val updateDiaryUseCase = UpdateDiaryUseCase(dataSource)
    private val getAllDiariesUseCase = GetAllDiariesUseCase(dataSource)

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        database.createDatabase()
        insertRandomDiaries(dataSource)
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Update valid diary entry returns 1 updated row`() = runTest {
        getAllDiariesUseCase().test {
            val originalList = awaitItem()

            var firstEntry = originalList.first()

            // verify that it isn't favorited
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
