package com.foreverrafs.superdiary.ui.favorite

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isInstanceOf
import com.foreverrafs.superdiary.TestAppDispatchers
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.data.usecase.GetFavoriteDiariesUseCase
import com.foreverrafs.superdiary.data.usecase.UpdateDiaryUseCase
import com.foreverrafs.superdiary.ui.feature.favorites.FavoriteViewModel
import com.foreverrafs.superdiary.ui.feature.favorites.screen.FavoriteScreenState
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteViewModelTest {
    private val dataSource: DataSource = mock<DataSource>()

    private lateinit var favoriteViewModel: FavoriteViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())

        everySuspend { dataSource.fetchFavorites() }.returns(flowOf(emptyList()))

        favoriteViewModel = FavoriteViewModel(
            getFavoriteDiariesUseCase = GetFavoriteDiariesUseCase(dataSource, TestAppDispatchers),
            updateDiaryUseCase = UpdateDiaryUseCase(dataSource, TestAppDispatchers),
            logger = AggregateLogger(emptyList()),
        )
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Success state is emitted after loading favorites`() = runTest {
        favoriteViewModel.state.test {
            // skip loading state
            skipItems(1)

            val successState = awaitItem()
            cancelAndIgnoreRemainingEvents()

            assertNotNull(successState)

            assertThat(successState).isInstanceOf<FavoriteScreenState.Content>()
        }
    }

    @Test
    fun `Success state is emitted even when there is no favorite`() = runTest {
        every { dataSource.fetchFavorites() }.returns(flowOf(emptyList()))

        favoriteViewModel.state.test {
            // skip loading state
            skipItems(1)

            val successState = awaitItem()
            cancelAndIgnoreRemainingEvents()
            assertNotNull(successState)

            assertThat(successState).isInstanceOf<FavoriteScreenState.Content>()
            assertThat((successState as FavoriteScreenState.Content).diaries).isEmpty()
        }
    }

    @Test
    fun `Removing favorite actually removes it`() = runTest {
        everySuspend { dataSource.update(any()) }.returns(1)

        favoriteViewModel.toggleFavorite(Diary("Hello"))

        verifySuspend { dataSource.update(any()) }
    }
}
