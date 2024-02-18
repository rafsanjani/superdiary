package com.foreverrafs.superdiary.ui.details

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isInstanceOf
import com.foreverrafs.superdiary.TestAppDispatchers
import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.data.usecase.DeleteDiaryUseCase
import com.foreverrafs.superdiary.ui.feature.details.DetailsViewModel
import io.mockative.Mock
import io.mockative.coEvery
import io.mockative.mock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModelTest {

    @Mock
    private val dataSource: DataSource = mock(DataSource::class)

    private lateinit var detailsViewModel: DetailsViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        detailsViewModel = DetailsViewModel(DeleteDiaryUseCase(dataSource, TestAppDispatchers))
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Successfully deleting a diary should emit deleted state`() = runTest {
        val diary = Diary("Hello world")

        coEvery { dataSource.delete(diary) }.returns(1)
        coEvery { dataSource.delete(listOf(diary)) }.returns(1)

        detailsViewModel.state.test {
            detailsViewModel.deleteDiary(diary)

            // Skip the initial idle state
            skipItems(1)
            val state = awaitItem()
            assertThat(state).isInstanceOf<DetailsViewModel.DetailsScreenState.DiaryDeleted>()
        }
    }

    @Test
    fun `Failing to delete shouldn't emit Deleted state`() = runTest {
        val diary = Diary("Hello world")

        coEvery { dataSource.delete(diary) }.returns(0)
        coEvery { dataSource.delete(listOf(diary)) }.returns(0)

        detailsViewModel.state.test {
            detailsViewModel.deleteDiary(diary)

            val state = awaitItem()
            cancelAndIgnoreRemainingEvents()
            assertThat(state).isInstanceOf<DetailsViewModel.DetailsScreenState.Idle>()
        }
    }
}
