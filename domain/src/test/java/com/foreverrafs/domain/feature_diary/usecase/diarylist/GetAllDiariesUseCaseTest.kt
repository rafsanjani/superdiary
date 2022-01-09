package com.foreverrafs.domain.feature_diary.usecase.diarylist

import app.cash.turbine.test
import com.foreverrafs.domain.feature_diary.Result.Success
import com.foreverrafs.domain.feature_diary.data.DependenciesInjector
import com.foreverrafs.domain.feature_diary.usecase.GetAllDiariesUseCase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class GetAllDiariesUseCaseTest {
    private lateinit var getAllDiaries: GetAllDiariesUseCase

    @Before
    fun before() {
        val repository = DependenciesInjector.`provideTestRepository()`()

        getAllDiaries = GetAllDiariesUseCase(repository)
    }

    @Test
    fun `fetch all diaries confirm fetched`() = runBlocking {
        getAllDiaries().test {
            val result = expectMostRecentItem()
            assertThat(result).isInstanceOf(Success::class.java)
            assertThat((result as Success).data).isNotEmpty()
        }
    }
}