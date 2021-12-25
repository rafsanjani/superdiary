package com.foreverrafs.superdiary.business.usecase.add

import app.cash.turbine.test
import com.foreverrafs.superdiary.business.Result
import com.foreverrafs.superdiary.business.data.DependenciesInjector
import com.foreverrafs.superdiary.business.model.Diary
import com.foreverrafs.superdiary.business.usecase.diarylist.GetAllDiariesUseCase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import kotlin.time.ExperimentalTime

class AddDiaryUseCaseTest {
    private lateinit var addDiary: AddDiaryUseCase
    private lateinit var getAllDiaries: GetAllDiariesUseCase

    @Before
    fun before() {
        val repository = DependenciesInjector.provideTestDataSource()

        addDiary = AddDiaryUseCase(repository)
        getAllDiaries = GetAllDiariesUseCase(repository)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `add new diary confirm added`(): Unit = runBlocking {
        addDiary(Diary(message = "Hello Brown Cow"))

        getAllDiaries().test {
            val result = expectItem()
            cancelAndConsumeRemainingEvents()
            assertThat(result).isInstanceOf(Result.Success::class.java)
            assertThat((result as Result.Success<List<Diary>>).data.size).isGreaterThan(1)
        }
    }
}