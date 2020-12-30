package com.foreverrafs.superdiary.business.usecase.common

import com.foreverrafs.superdiary.business.data.DependenciesInjector
import com.foreverrafs.superdiary.business.usecase.diarylist.GetAllDiariesUseCase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

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

    @Test
    fun `delete diary confirm deleted`() = runBlocking {
        var items = getAllDiaries().toList()
        val initialSize = items.first().size

        val diaryToDelete = items.first().first()
        val response = deleteDiary(diaryToDelete)


        items = getAllDiaries().toList()

        assertThat(response).isEqualTo(1)
        assertThat(items.first().size).isLessThan(initialSize)
    }
}