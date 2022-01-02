package com.foreverrafs.superdiary.business.usecase.common

import app.cash.turbine.test
import com.foreverrafs.superdiary.business.Result
import com.foreverrafs.superdiary.business.data.DependenciesInjector
import com.foreverrafs.superdiary.business.usecase.DeleteDiaryUseCase
import com.foreverrafs.superdiary.business.usecase.GetAllDiariesUseCase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
class DeleteDiaryUseCaseTest {

    private lateinit var deleteDiary: DeleteDiaryUseCase
    private lateinit var getAllDiaries: GetAllDiariesUseCase

    @Before
    fun before() {
        val repository = DependenciesInjector.provideTestDataSource()

        deleteDiary = DeleteDiaryUseCase(repository)
        getAllDiaries = GetAllDiariesUseCase(repository)
    }

    @ExperimentalTime
    @Test
    fun `delete diary confirm deleted`() = runBlocking {
        getAllDiaries().test {
            val items = (expectMostRecentItem() as Result.Success).data

            val initialSize = items.size

            val diaryToDelete = items.first()
            val response = deleteDiary(diaryToDelete)

            val updatedItems = getAllDiaries().toList().first()

            assertThat(response).isInstanceOf(Result.Success::class.java)
            assertThat((updatedItems as Result.Success).data.size).isLessThan(initialSize)
            cancelAndIgnoreRemainingEvents()
        }
    }
}