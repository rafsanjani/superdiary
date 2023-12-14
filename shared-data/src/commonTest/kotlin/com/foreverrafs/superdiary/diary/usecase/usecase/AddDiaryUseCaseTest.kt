package com.foreverrafs.superdiary.diary.usecase.usecase

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotEmpty
import com.foreverrafs.superdiary.diary.Database
import com.foreverrafs.superdiary.diary.Result
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.datasource.LocalDataSource
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.usecase.AddDiaryUseCase
import com.foreverrafs.superdiary.diary.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.diary.usecase.datasource.TestDatabaseDriver
import com.foreverrafs.superdiary.diary.validator.DiaryValidator
import com.foreverrafs.superdiary.diary.validator.DiaryValidatorImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class AddDiaryUseCaseTest {
    private val database = Database(TestDatabaseDriver())
    private val dataSource: DataSource = LocalDataSource(database)
    private val validator: DiaryValidator = DiaryValidatorImpl(Clock.System)

    private val getAllDiariesUseCase = GetAllDiariesUseCase(dataSource)
    private val addDiaryUseCase = AddDiaryUseCase(dataSource, validator)

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        database.createDatabase()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Add new diary today and confirm saved`() = runTest {
        val diary = Diary(
            id = 1000L,
            entry = "New Entry today",
            date = Clock.System.now(),
            isFavorite = false,
        )
        addDiaryUseCase(diary)

        getAllDiariesUseCase().test {
            val items = awaitItem()
            cancelAndConsumeRemainingEvents()

            assertThat(items).isNotEmpty()
            assertThat(items.first().id).isEqualTo(1000L)
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

    @Test
    fun `Add new relaxed diary and confirm saved`() = runTest {
        val relaxedAddDiaryUseCase = AddDiaryUseCase(dataSource) {}

        val diary = Diary(
            id = 1200L,
            entry = "New Entry",
            date = Instant.parse("2023-03-03T03:33:25.587Z"),
            isFavorite = false,
        )

        relaxedAddDiaryUseCase(diary)

        getAllDiariesUseCase().test {
            val items = awaitItem()
            cancelAndConsumeRemainingEvents()

            assertThat(items).contains(diary)
        }
    }
}
