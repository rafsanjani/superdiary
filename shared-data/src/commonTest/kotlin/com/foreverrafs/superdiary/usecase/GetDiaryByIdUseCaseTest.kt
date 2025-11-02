package com.foreverrafs.superdiary.usecase

import assertk.assertThat
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.foreverrafs.superdiary.data.datasource.LocalDataSource
import com.foreverrafs.superdiary.database.Database
import com.foreverrafs.superdiary.database.testSuperDiaryDatabase
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.repository.DataSource
import com.foreverrafs.superdiary.domain.usecase.GetDiaryByIdUseCase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
class GetDiaryByIdUseCaseTest {
    private val dataSource: DataSource = LocalDataSource(Database(testSuperDiaryDatabase))
    private val getDiaryByIdUseCase = GetDiaryByIdUseCase(dataSource = dataSource)


    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Should return diary data for valid id`() = runTest {
        dataSource.save(
            diary = Diary(id = 12L, entry = "Hello World!"),
        )

        val diary = getDiaryByIdUseCase(id = 12L)

        assertThat(diary).isNotNull()
    }

    @Test
    fun `Should return null for invalid ID`() = runTest {
        dataSource.save(
            diary = Diary(
                id = 12L,
                entry = "Hello World!",
            ),
        )

        val diary = getDiaryByIdUseCase(id = 112L)

        assertThat(diary).isNull()
    }
}
