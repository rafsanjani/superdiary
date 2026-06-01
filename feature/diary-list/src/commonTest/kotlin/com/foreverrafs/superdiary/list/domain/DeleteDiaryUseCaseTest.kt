package com.foreverrafs.superdiary.list.domain

import assertk.assertThat
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.foreverrafs.superdiary.common.coroutines.TestAppDispatchers
import com.foreverrafs.superdiary.data.datasource.LocalDataSource
import com.foreverrafs.superdiary.database.Database
import com.foreverrafs.superdiary.database.testSuperDiaryDatabase
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.repository.DataSource
import com.foreverrafs.superdiary.domain.usecase.GetDiaryByIdUseCase
import com.foreverrafs.superdiary.list.domain.usecase.DeleteDiaryUseCase
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
class DeleteDiaryUseCaseTest {
    private val database = Database(testSuperDiaryDatabase)
    private val dataSource: DataSource = LocalDataSource(database)

    private val getDiaryByIdUseCase = GetDiaryByIdUseCase(dataSource)
    private val deleteDiaryUseCase = DeleteDiaryUseCase(dataSource, TestAppDispatchers)

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Delete diary and confirm deletion`() = runTest {
        dataSource.save(Diary(entry = "hello entry", id = 100L))

        val entry = getDiaryByIdUseCase(id = 100L)

        assertThat(entry).isNotNull()

        deleteDiaryUseCase(listOf(entry!!))

        assertThat(getDiaryByIdUseCase(id = 100L)).isNull()
    }

    @Test
    fun `Delete multiple diaries actually deletes them`() = runTest {
        val diaries = (0..10).map {
            Diary(id = it.toLong(), entry = "Entry number $it")
        }

        dataSource.save(diaries)

        deleteDiaryUseCase(diaries.take(5))

        assertThat(getDiaryByIdUseCase(1L)).isNull()
        assertThat(getDiaryByIdUseCase(2L)).isNull()
    }
}
