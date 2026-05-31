package com.foreverrafs.superdiary.list.domain

import assertk.assertThat
import assertk.assertions.isTrue
import com.foreverrafs.superdiary.common.coroutines.TestAppDispatchers
import com.foreverrafs.superdiary.data.datasource.LocalDataSource
import com.foreverrafs.superdiary.database.Database
import com.foreverrafs.superdiary.database.testSuperDiaryDatabase
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.repository.DataSource
import com.foreverrafs.superdiary.domain.usecase.UpdateDiaryUseCase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteDiariesUseCaseTest {
    private val database = Database(testSuperDiaryDatabase)
    private val dataSource: DataSource = LocalDataSource(database)

    private val updateDiariesUseCase = UpdateDiaryUseCase(dataSource, TestAppDispatchers)

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(TestAppDispatchers.main)
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Update favorite flag of diaries updates them on db`() = runTest {
        val diary = Diary(entry = "Entry", id = 100L, isFavorite = false)
        dataSource.save(diary)

        updateDiariesUseCase(diary.copy(isFavorite = true))

        assertThat(diary.isFavorite).isTrue()
    }
}
