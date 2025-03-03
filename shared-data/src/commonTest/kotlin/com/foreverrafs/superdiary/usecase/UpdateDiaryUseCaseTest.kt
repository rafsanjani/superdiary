package com.foreverrafs.superdiary.usecase

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.foreverrafs.superdiary.common.coroutines.TestAppDispatchers
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.data.datasource.LocalDataSource
import com.foreverrafs.superdiary.database.Database
import com.foreverrafs.superdiary.database.testSuperDiaryDatabase
import com.foreverrafs.superdiary.domain.repository.DataSource
import com.foreverrafs.superdiary.domain.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.domain.usecase.UpdateDiaryUseCase
import com.foreverrafs.superdiary.insertRandomDiaries
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
class UpdateDiaryUseCaseTest {
    private val database = Database(testSuperDiaryDatabase)
    private val dataSource: DataSource = LocalDataSource(database)

    private val updateDiaryUseCase =
        UpdateDiaryUseCase(dataSource = dataSource, dispatchers = TestAppDispatchers)
    private val getAllDiariesUseCase =
        GetAllDiariesUseCase(dataSource = dataSource, dispatchers = TestAppDispatchers)

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Update valid diary entry returns 1 updated row`() = runTest {
        insertRandomDiaries(dataSource)

        getAllDiariesUseCase().test {
            val originalList = (awaitItem() as Result.Success).data

            var firstEntry = originalList.first()

            // verify that it isn't favorite
            assertThat(firstEntry.isFavorite).isFalse()

            val updated = updateDiaryUseCase(
                firstEntry.copy(
                    isFavorite = true,
                ),
            )

            // fetch the remaining diaries
            val currentList = (awaitItem() as Result.Success).data

            firstEntry = currentList.first()

            // verify that it has been updated and changed to favorite = true
            assertThat(firstEntry.isFavorite).isTrue()

            val result = updated as Result.Success
            assertThat(result.data).isTrue()
        }
    }
}
