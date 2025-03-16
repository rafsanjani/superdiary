package com.foreverrafs.superdiary.list.data

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.foreverrafs.superdiary.common.coroutines.TestAppDispatchers
import com.foreverrafs.superdiary.data.Result.Success
import com.foreverrafs.superdiary.database.Database
import com.foreverrafs.superdiary.database.testSuperDiaryDatabase
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.model.toDatabase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

class DiaryListRepositoryImplTest {

    private val database: Database = Database(testSuperDiaryDatabase)
    private lateinit var repository: DiaryListRepositoryImpl

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(TestAppDispatchers.main)
        repository = DiaryListRepositoryImpl(database)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getDiaryById returns diary when found`() {
        val diary = Diary(id = 123L, entry = "Hello World")

        database.insert(diary.toDatabase())

        val result = repository.getDiaryById(123L)

        assertThat(result).isNotNull()
    }

    @Test
    fun `getDiaryById returns null when not found`() {
        val result = repository.getDiaryById(1L)

        assertThat(result).isNull()
    }

    @Test
    fun `getAllDiaries returns mapped flow`() = runTest {
        database.insert(
            Diary(id = 1L, entry = "Hello World").toDatabase(),
        )

        val result = repository.getAllDiaries()

        result.test {
            val items = awaitItem()
            assertThat(items).isNotEmpty()
        }
    }

    @Test
    fun `updateDiary returns success when update succeeds`() = runTest {
        val diary = Diary(id = 1L, entry = "Hello World")

        val result = repository.updateDiary(diary)

        assertThat(result).isInstanceOf(Success::class)
    }
}
