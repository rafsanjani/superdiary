package com.foreverrafs.superdiary.usecase

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.foreverrafs.superdiary.common.coroutines.TestAppDispatchers
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.data.datasource.LocalDataSource
import com.foreverrafs.superdiary.database.Database
import com.foreverrafs.superdiary.database.testSuperDiaryDatabase
import com.foreverrafs.superdiary.domain.NoOpSynchronizer
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.repository.DataSource
import com.foreverrafs.superdiary.domain.usecase.AddDiaryUseCase
import com.foreverrafs.superdiary.domain.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.domain.validator.DiaryValidator
import com.foreverrafs.superdiary.domain.validator.DiaryValidatorImpl
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus

@OptIn(ExperimentalTime::class)
class AddDiaryUseCaseTest {
    private val database = Database(testSuperDiaryDatabase)
    private val dataSource: DataSource = LocalDataSource(database)
    private val validator: DiaryValidator = DiaryValidatorImpl(Clock.System)

    private val getAllDiariesUseCase = GetAllDiariesUseCase(dataSource, TestAppDispatchers)
    private val addDiaryUseCase =
        AddDiaryUseCase(
            dataSource = dataSource,
            dispatchers = TestAppDispatchers,
            synchronizer = NoOpSynchronizer,
            validator = validator,
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(TestAppDispatchers.main)
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

            assertThat(items).isInstanceOf(Result.Success::class)
            val firstItem = (items as? Result.Success)?.data?.first()
            assertThat(firstItem?.id).isEqualTo(1000L)
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
        val relaxedAddDiaryUseCase = AddDiaryUseCase(
            dataSource = dataSource,
            dispatchers = TestAppDispatchers,
            validator = {},
        )

        val diary = Diary(
            id = 1200L,
            entry = "New Entry",
            date = kotlin.time.Instant.parse("2023-03-03T03:33:25.587Z"),
            isFavorite = false,
        )

        relaxedAddDiaryUseCase(diary)

        getAllDiariesUseCase().test {
            val items = awaitItem()
            cancelAndConsumeRemainingEvents()

            assertThat(items).isInstanceOf(Result.Success::class)
        }
    }
}
