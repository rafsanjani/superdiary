package com.foreverrafs.superdiary.business.usecase.add

import com.foreverrafs.superdiary.business.data.DependenciesInjector
import com.foreverrafs.superdiary.business.model.Diary
import com.foreverrafs.superdiary.business.usecase.diarylist.GetAllDiariesUseCase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class AddDiaryUseCaseTest {
    private lateinit var addDiary: AddDiaryUseCase
    private lateinit var getAllDiaries: GetAllDiariesUseCase

    @Before
    fun before() {
        val repository = DependenciesInjector.provideTestDataSource()

        addDiary = AddDiaryUseCase(repository)
        getAllDiaries = GetAllDiariesUseCase(repository)
    }

    @Test
    fun `add new diary confirm added`() = runBlocking {
        var items = getAllDiaries().toList()
        val initialSize = items.first().size

        val response = addDiary(Diary(message = "Hello Brown Cow"))
        items = getAllDiaries().toList()

        assertThat(response).isEqualTo(1)
        assertThat(items.first().size).isGreaterThan(initialSize)
    }
}