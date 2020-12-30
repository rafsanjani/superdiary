package com.foreverrafs.superdiary.business.usecase.diarylist

import com.foreverrafs.superdiary.business.data.DependenciesInjector
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.collect
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
        getAllDiaries().collect {
            assertThat(it).isNotEmpty()
        }
    }
}