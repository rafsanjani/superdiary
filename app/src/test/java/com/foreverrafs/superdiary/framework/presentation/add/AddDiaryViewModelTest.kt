package com.foreverrafs.superdiary.framework.presentation.add

import app.cash.turbine.test
import com.foreverrafs.domain.feature_diary.data.DependenciesInjector
import com.foreverrafs.domain.feature_diary.model.Diary
import com.foreverrafs.superdiary.ui.feature_diary.add.AddDiaryState
import com.foreverrafs.superdiary.ui.feature_diary.add.AddDiaryViewModel
import com.foreverrafs.superdiary.util.MockPreferenceDataStore
import com.foreverrafs.superdiary.util.rules.CoroutineTestRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
class AddDiaryViewModelTest {
    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private val dataStore = MockPreferenceDataStore()

    private val addDiaryViewModel = AddDiaryViewModel(
        dispatcher = coroutineRule.testDispatcher,
        addDiary = DependenciesInjector.provideAddDiaryUseCase(),
        dataStore = dataStore
    )


    @ExperimentalTime
    @Test
    fun `save diary confirm saved`() = runBlockingTest {
        addDiaryViewModel.saveDiary(
            Diary(
                message = "Test Diary",
                title = ""
            )
        )

        addDiaryViewModel.viewState.test {
            assertThat(expectMostRecentItem()).isInstanceOf(AddDiaryState.Success::class.java)
        }
    }
}