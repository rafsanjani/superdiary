package com.foreverrafs.domain.feature_diary.usecase.add

import app.cash.turbine.test
import com.foreverrafs.domain.feature_diary.Result.Success
import com.foreverrafs.domain.feature_diary.data.DependenciesInjector
import com.foreverrafs.domain.feature_diary.model.Diary
import com.foreverrafs.domain.feature_diary.usecase.AddDiaryUseCase
import com.foreverrafs.domain.feature_diary.usecase.GetAllDiariesUseCase
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
        val repository = DependenciesInjector.`provideTestRepository()`()

        addDiary = AddDiaryUseCase(repository)
        getAllDiaries = GetAllDiariesUseCase(repository)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `add new diary confirm added`(): Unit = runBlocking {
        addDiary(
            Diary(
                message = "Hello Brown Cow",
                title = ""
            )
        )

        getAllDiaries().test {
            val result = expectMostRecentItem()
            cancelAndConsumeRemainingEvents()
            assertThat(result).isInstanceOf(Success::class.java)
            assertThat((result as Success<List<Diary>>).data.size).isGreaterThan(
                1
            )
        }
    }
}