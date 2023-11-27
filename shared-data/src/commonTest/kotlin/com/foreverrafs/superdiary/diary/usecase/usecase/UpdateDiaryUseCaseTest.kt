package com.foreverrafs.superdiary.diary.usecase.usecase

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.diary.usecase.UpdateDiaryUseCase
import com.foreverrafs.superdiary.diary.usecase.datasource.InMemoryDataSource
import com.foreverrafs.superdiary.diary.usecase.insertRandomDiaries
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class UpdateDiaryUseCaseTest {
    private val dataSource: DataSource = InMemoryDataSource()
    private val updateDiaryUseCase = UpdateDiaryUseCase(dataSource)
    private val getAllDiariesUseCase = GetAllDiariesUseCase(dataSource)

    @BeforeTest
    fun setup() {
        insertRandomDiaries(dataSource)
    }

    @Test
    fun `Update valid diary entry returns 1 updated row`() = runTest {
        getAllDiariesUseCase().test {
            val originalList = awaitItem()

            var firstEntry = originalList.first()

            // verify that it isnt' favorited
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
