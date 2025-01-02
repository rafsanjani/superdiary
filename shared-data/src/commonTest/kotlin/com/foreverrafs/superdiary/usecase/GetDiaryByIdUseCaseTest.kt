package com.foreverrafs.superdiary.usecase

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.foreverrafs.superdiary.common.coroutines.TestAppDispatchers
import com.foreverrafs.superdiary.data.datasource.LocalDataSource
import com.foreverrafs.superdiary.database.Database
import com.foreverrafs.superdiary.database.testSuperDiaryDatabase
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.repository.DataSource
import com.foreverrafs.superdiary.domain.usecase.AddDiaryUseCase
import com.foreverrafs.superdiary.domain.usecase.GetDiaryByIdUseCase
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
class GetDiaryByIdUseCaseTest {
    private val database = Database(testSuperDiaryDatabase)
    private val dataSource: DataSource = LocalDataSource(database = database)
    private val addDiaryUseCase = AddDiaryUseCase(dataSource, TestAppDispatchers) {
        // no-op validator
    }
    private val getDiaryByIdUseCase =
        GetDiaryByIdUseCase(dataSource = dataSource, dispatchers = TestAppDispatchers)

    @BeforeTest
    fun setup() {
        database.clearDiaries()
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Getting a dairy by id should return it`() = runTest {
        // Insert a diary into the database
        addDiaryUseCase(
            Diary(
                id = 12345L,
                entry = "Hello World",
            ),
        )

        // Fetch the diary we just inserted
        getDiaryByIdUseCase(id = 12345L).test {
            val diary = awaitItem()

            // Assert that the diary is not null
            assertThat(diary).isNotNull()
            assertThat(diary?.id).isEqualTo(12345L)
        }
    }

    @Test
    fun `Getting invalid diary id should return null`() = runTest {
        // Insert a diary into the database
        addDiaryUseCase(
            Diary(
                id = 12345L,
                entry = "Hello World",
            ),
        )

        // Attempt to fetch a non-existent diary by id
        getDiaryByIdUseCase(id = 122222L).test {
            val diary = awaitItem()

            // Assert that the diary is not null
            assertThat(diary).isNull()
        }
    }
}
