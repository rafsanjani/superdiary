package com.foreverrafs.superdiary.usecase

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import com.foreverrafs.superdiary.common.coroutines.TestAppDispatchers
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.data.datasource.LocalDataSource
import com.foreverrafs.superdiary.database.Database
import com.foreverrafs.superdiary.database.testSuperDiaryDatabase
import com.foreverrafs.superdiary.domain.repository.DataSource
import com.foreverrafs.superdiary.domain.usecase.GetFavoriteDiariesUseCase
import com.foreverrafs.superdiary.domain.usecase.UpdateDiaryUseCase
import com.foreverrafs.superdiary.insertRandomDiaries
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteDiariesUseCaseTest {
    private val database = Database(testSuperDiaryDatabase)
    private val dataSource: DataSource = LocalDataSource(database)

    private val updateDiariesUseCase = UpdateDiaryUseCase(dataSource, TestAppDispatchers)
    private val getAllDiariesUseCase = GetAllDiariesUseCase(dataSource, TestAppDispatchers)
    private val getFavoriteDiariesUseCase = GetFavoriteDiariesUseCase(
        dataSource,
        TestAppDispatchers,
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(TestAppDispatchers.main)
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Update favorite flag of diaries updates them on db`() = runTest {
        insertRandomDiaries(dataSource)
        getAllDiariesUseCase().test {
            val diaries = (awaitItem() as Result.Success).data

            val favoriteDiaries = diaries
                .take(4)
                .map { it.copy(isFavorite = true) }

            favoriteDiaries.forEach {
                updateDiariesUseCase(it)
            }
            cancelAndConsumeRemainingEvents()
        }

        getFavoriteDiariesUseCase().test {
            val favorites = awaitItem()
            assertThat(favorites).hasSize(4)
            cancelAndConsumeRemainingEvents()
        }
    }
}
