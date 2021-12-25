package com.foreverrafs.superdiary.business.usecase.diarylist

import com.foreverrafs.superdiary.business.data.DependenciesInjector
import org.junit.Before

class GetAllDiariesUseCaseTest {
    private lateinit var getAllDiaries: GetAllDiariesUseCase

    @Before
    fun before() {
        val repository = DependenciesInjector.provideTestDataSource()

        getAllDiaries = GetAllDiariesUseCase(repository)
    }

//    @Test
//    fun `fetch all diaries confirm fetched`() = runBlocking {
//        getAllDiaries().collect {
//            assertThat(it).isNotEmpty()
//        }
//    }
}