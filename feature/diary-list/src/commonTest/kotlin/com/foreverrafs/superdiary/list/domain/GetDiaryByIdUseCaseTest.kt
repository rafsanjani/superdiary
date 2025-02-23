package com.foreverrafs.superdiary.list.domain

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
import com.foreverrafs.superdiary.list.data.DiaryListRepositoryImpl
import com.foreverrafs.superdiary.list.domain.usecase.GetDiaryByIdUseCase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    private val getDiaryByIdUseCase = GetDiaryByIdUseCase(
        repository = DiaryListRepositoryImpl(
            database,
        ),
    )

    @BeforeTest
    fun setup() {
        database.clearDiaries()
        Dispatchers.setMain(TestAppDispatchers.main)
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Getting a dairy by id should return it`() = runTest {
        // Insert a diary into the database
        val result = addDiaryUseCase(
            Diary(
                id = 12345L,
                entry = "Hello World",
            ),
        )

        println(result)
        // Fetch the diary we just inserted
        val diary = getDiaryByIdUseCase(id = 12345L)

        // Assert that the diary is not null
        assertThat(diary).isNotNull()
        assertThat(diary?.id).isEqualTo(12345L)
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
        val diary = getDiaryByIdUseCase(id = 122222L)

        // Assert that the diary is not null
        assertThat(diary).isNull()
    }
}
