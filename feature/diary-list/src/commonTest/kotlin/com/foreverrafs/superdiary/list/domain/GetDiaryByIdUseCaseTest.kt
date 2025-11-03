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
import com.foreverrafs.superdiary.list.data.DiaryListRepositoryImpl
import com.foreverrafs.superdiary.list.domain.usecase.GetDiaryByIdUseCase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
class GetDiaryByIdUseCaseTest {
    private val database = Database(testSuperDiaryDatabase)
    private val dataSource: DataSource = LocalDataSource(database = database)

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
        dataSource.save(
            Diary(
                id = 12345L,
                entry = "Hello World",
            ),
        )

        // Fetch the diary we just inserted
        val diary = getDiaryByIdUseCase(id = 12345L)

        // Assert that the diary is not null
        assertThat(diary).isNotNull()
        assertThat(diary?.id).isEqualTo(12345L)
    }

    @Test
    fun `Getting invalid diary id should return null`() = runTest {
        // Insert a diary into the database
        dataSource.save(
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
