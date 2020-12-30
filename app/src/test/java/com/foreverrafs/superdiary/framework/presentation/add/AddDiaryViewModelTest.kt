package com.foreverrafs.superdiary.framework.presentation.add

import app.cash.turbine.test
import com.foreverrafs.superdiary.business.data.DependenciesInjector
import com.foreverrafs.superdiary.business.model.Diary
import com.foreverrafs.superdiary.framework.presentation.add.state.AddDiaryState
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
        addDiaryViewModel.saveDiary(Diary(message = "Test Diary"))

        addDiaryViewModel.viewState.test {
            assertThat(expectItem()).isInstanceOf(AddDiaryState.Saved::class.java)
        }
    }
}