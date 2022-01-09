package com.foreverrafs.superdiary.framework.presentation.add

import app.cash.turbine.test
import com.foreverrafs.domain.business.data.DependenciesInjector
import com.foreverrafs.domain.business.model.Diary
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
        addDiary = com.foreverrafs.domain.business.data.DependenciesInjector.provideAddDiaryUseCase(),
        dataStore = dataStore
    )


    @ExperimentalTime
    @Test
    fun `save diary confirm saved`() = runBlockingTest {
        addDiaryViewModel.saveDiary(
            com.foreverrafs.domain.business.model.Diary(
                message = "Test Diary",
                title = ""
            )
        )

        addDiaryViewModel.viewEvent.test {
            assertThat(expectMostRecentItem()).isInstanceOf(AddDiaryState.Saved::class.java)
        }
    }
}