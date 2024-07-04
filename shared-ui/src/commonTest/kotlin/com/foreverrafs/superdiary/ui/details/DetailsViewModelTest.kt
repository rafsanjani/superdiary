package com.foreverrafs.superdiary.ui.details

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import com.foreverrafs.superdiary.TestAppDispatchers
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.data.usecase.DeleteDiaryUseCase
import com.foreverrafs.superdiary.data.usecase.GetDiaryByIdUseCase
import com.foreverrafs.superdiary.ui.feature.details.DeleteDiaryState
import com.foreverrafs.superdiary.ui.feature.details.DetailsViewModel
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
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

    private val dataSource: DataSource = mock<DataSource>()

    private lateinit var detailsViewModel: DetailsViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        detailsViewModel = DetailsViewModel(
            DeleteDiaryUseCase(
                dataSource = dataSource,
                dispatchers = TestAppDispatchers,
            ),
            getDiaryByIdUseCase = GetDiaryByIdUseCase(
                dataSource = dataSource,
                dispatchers = TestAppDispatchers,
            ),
            logger = AggregateLogger(emptyList()),
        )
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Successfully deleting a diary should emit deleted state`() = runTest {
        val diary = Diary("Hello world")

        everySuspend { dataSource.delete(diary) }.returns(1)
        everySuspend { dataSource.delete(listOf(diary)) }.returns(1)

        detailsViewModel.deleteDiaryState.test {
            detailsViewModel.deleteDiary(diary)

            // Skip the initial null idle state
            skipItems(1)
            val state = awaitItem()
            assertThat(state)
                .isNotNull()
                .isInstanceOf<DeleteDiaryState.Success>()
        }
    }

    @Test
    fun `Failing to delete should emit failure deleting state`() = runTest {
        val diary = Diary("Hello world")

        everySuspend { dataSource.delete(diary) }.returns(0)
        everySuspend { dataSource.delete(listOf(diary)) }.returns(0)

        detailsViewModel.deleteDiaryState.test {
            detailsViewModel.deleteDiary(diary)

            val state = awaitItem()
            cancelAndIgnoreRemainingEvents()
            assertThat(state)
                .isNotNull()
                .isInstanceOf<DeleteDiaryState.Failure>()
        }
    }
}
