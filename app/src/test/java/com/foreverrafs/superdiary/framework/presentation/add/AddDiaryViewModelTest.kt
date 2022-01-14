package com.foreverrafs.superdiary.framework.presentation.add

import app.cash.turbine.test
import com.foreverrafs.domain.feature_diary.Result
import com.foreverrafs.domain.feature_diary.model.Diary
import com.foreverrafs.domain.feature_diary.repository.Repository
import com.foreverrafs.domain.feature_diary.usecase.AddDiaryUseCase
import com.foreverrafs.superdiary.ui.feature_diary.add.AddDiaryState
import com.foreverrafs.superdiary.ui.feature_diary.add.AddDiaryViewModel
import com.foreverrafs.superdiary.util.MockPreferenceDataStore
import com.foreverrafs.superdiary.util.rules.CoroutineTestRule
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class AddDiaryViewModelTest {
    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private val dataStore = MockPreferenceDataStore()
    private val repository: Repository = mockk()

    private val addDiaryViewModel = AddDiaryViewModel(
        dispatcher = coroutineRule.testDispatcher,
        addDiary = AddDiaryUseCase(repository),
        dataStore = dataStore
    )


    @Test
    fun `save diary confirm success state`() = runTest {
        coEvery {
            repository.add(any())
        } returns Result.Success(1)

        addDiaryViewModel.saveDiary(
            Diary(
                message = "Test Diary",
                title = ""
            )
        )

        addDiaryViewModel.viewState.test {
            val initialState = awaitItem()
            val finalState = awaitItem()

            assertThat(initialState).isNull()
            assertThat(finalState).isInstanceOf(AddDiaryState.Success::class.java)
        }
    }

    @Test
    fun `save diary confirm failure state`() = runTest {
        coEvery {
            repository.add(any())
        } returns Result.Error(Exception("Error Saving Diary"))

        addDiaryViewModel.saveDiary(
            Diary(
                message = "Test Diary",
                title = ""
            )
        )

        addDiaryViewModel.viewState.test {
            val initialState = awaitItem()
            val finalState = awaitItem()

            assertThat(initialState).isNull()
            assertThat(finalState).isInstanceOf(AddDiaryState.Error::class.java)
        }
    }
}