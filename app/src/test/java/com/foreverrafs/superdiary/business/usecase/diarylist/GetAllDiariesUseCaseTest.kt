package com.foreverrafs.superdiary.business.usecase.diarylist

import app.cash.turbine.test
import com.foreverrafs.superdiary.business.Result
import com.foreverrafs.superdiary.business.data.DependenciesInjector
import com.foreverrafs.superdiary.business.usecase.GetAllDiariesUseCase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class GetAllDiariesUseCaseTest {
    private lateinit var getAllDiaries: GetAllDiariesUseCase

    @Before
    fun before() {
        val repository = DependenciesInjector.provideTestDataSource()

        getAllDiaries = GetAllDiariesUseCase(repository)
    }

    @Test
    fun `fetch all diaries confirm fetched`() = runBlocking {
        getAllDiaries().test {
            val result = expectMostRecentItem()
            assertThat(result).isInstanceOf(Result.Success::class.java)
            assertThat((result as Result.Success).data).isNotEmpty()
        }
    }
}