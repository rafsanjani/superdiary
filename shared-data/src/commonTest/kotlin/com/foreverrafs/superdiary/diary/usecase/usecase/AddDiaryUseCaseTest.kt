package com.foreverrafs.superdiary.diary.usecase.usecase

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isInstanceOf
import com.foreverrafs.superdiary.diary.Result
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.usecase.AddDiaryUseCase
import com.foreverrafs.superdiary.diary.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.diary.usecase.InMemoryDataSource
import com.foreverrafs.superdiary.diary.utils.DiaryValidator
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlin.random.Random
import kotlin.test.Test

class AddDiaryUseCaseTest {
    private val dataSource: DataSource = InMemoryDataSource()
    private val validator: DiaryValidator = DiaryValidator(Clock.System)

    private val getAllDiariesUseCase = GetAllDiariesUseCase(dataSource)
    private val addDiaryUseCase = AddDiaryUseCase(dataSource, validator)

    @Test
    fun `Add new diary today and confirm saved`() = runTest {
        val diary = Diary(
            id = Random.nextLong(),
            entry = "New Entry today",
            date = Clock.System.now(),
            isFavorite = false,
        )

        addDiaryUseCase(diary)

        getAllDiariesUseCase().test {
            val items = awaitItem()
            cancelAndConsumeRemainingEvents()

            assertThat(items).contains(diary)
        }
    }

    @Test
    fun `Adding a new diary in the future fails`() = runTest {
        val today = Clock.System.now()

        val diary = Diary(
            entry = "New Entry",
            date = today.plus(1, DateTimeUnit.DAY, TimeZone.currentSystemDefault()),
            isFavorite = false,
        )

        val result = addDiaryUseCase(diary)

        assertThat(result).isInstanceOf(Result.Failure::class)
    }

    @Test
    fun `Adding a new diary in the past fails`() = runTest {
        val today = Clock.System.now()

        val diary = Diary(
            entry = "New Entry",
            date = today.minus(1, DateTimeUnit.DAY, TimeZone.currentSystemDefault()),
            isFavorite = false,
        )

        val result = addDiaryUseCase(diary)

        assertThat(result).isInstanceOf(Result.Failure::class)
    }
}
