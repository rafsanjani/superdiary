package com.foreverrafs.superdiary.diary.usecase.usecase

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.usecase.AddDiaryUseCase
import com.foreverrafs.superdiary.diary.usecase.GetLatestEntriesUseCase
import com.foreverrafs.superdiary.diary.usecase.datasource.InMemoryDataSource
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.random.Random
import kotlin.test.Test

class GetLatestEntriesUseCaseTest {
    private val dataSource: DataSource = InMemoryDataSource()

    private val addDiaryUseCase = AddDiaryUseCase(dataSource) {
        // no-op validator
    }
    private val getLatestEntriesUseCase = GetLatestEntriesUseCase(dataSource)

    @Test
    fun `Last inserted item should be at the top`() = runTest {
        // Insert first item
        addDiaryUseCase(
            diary = Diary(
                id = Random.nextLong(),
                entry = "Latest Diary Entry #1",
                date = Clock.System.now(),
                isFavorite = false,
            ),
        )

        // Insert second item
        addDiaryUseCase(
            diary = Diary(
                id = Random.nextLong(),
                entry = "Latest Diary Entry #2",
                date = Clock.System.now(),
                isFavorite = false,
            ),
        )

        getLatestEntriesUseCase(1).test {
            val diaries = awaitItem()
            cancelAndConsumeRemainingEvents()

            assertThat(diaries).isNotEmpty()
            assertThat(diaries[0].entry).isEqualTo("Latest Diary Entry #2")
        }
    }
}
