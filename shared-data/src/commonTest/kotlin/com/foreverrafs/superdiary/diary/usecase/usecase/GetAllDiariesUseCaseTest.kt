package com.foreverrafs.superdiary.diary.usecase.usecase

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.diary.usecase.datasource.InMemoryDataSource
import com.foreverrafs.superdiary.diary.usecase.insertRandomDiaries
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class GetAllDiariesUseCaseTest {
    private val dataSource: DataSource = InMemoryDataSource()
    private val getAllDiariesUseCase = GetAllDiariesUseCase(dataSource)

    @BeforeTest
    fun setup() {
        insertRandomDiaries(dataSource)
    }

    @Test
    fun `Getting all diaries should return all entries`() = runTest {
        getAllDiariesUseCase().test {
            val diaries = awaitItem()

            assertThat(diaries).isNotEmpty()
            assertThat(diaries[5].entry).isEqualTo("Diary Entry #5")
        }
    }
}
