package com.foreverrafs.superdiary.ui.favorite

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotEmpty
import com.foreverrafs.superdiary.TestAppDispatchers
import com.foreverrafs.superdiary.core.logging.Logger
import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.data.usecase.GetFavoriteDiariesUseCase
import com.foreverrafs.superdiary.data.usecase.UpdateDiaryUseCase
import com.foreverrafs.superdiary.ui.feature.favorites.model.FavoriteViewModel
import com.foreverrafs.superdiary.ui.feature.favorites.screen.FavoriteScreenState
import io.mockative.Mock
import io.mockative.any
import io.mockative.coEvery
import io.mockative.coVerify
import io.mockative.every
import io.mockative.mock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock

@Ignore
@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteViewModelTest {
    @Mock
    private val dataSource: DataSource = mock(DataSource::class)

    private lateinit var favoriteViewModel: FavoriteViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())

        favoriteViewModel = FavoriteViewModel(
            getFavoriteDiariesUseCase = GetFavoriteDiariesUseCase(dataSource, TestAppDispatchers),
            updateDiaryUseCase = UpdateDiaryUseCase(dataSource, TestAppDispatchers),
            logger = Logger,
        )
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Success state is emitted after loading favorites`() = runTest {
        every { dataSource.fetchFavorites() }.returns(
            flowOf(
                listOf(
                    Diary(
                        entry = "Fake Diary",
                        date = Clock.System.now(),
                        isFavorite = true,
                    ),
                ),
            ),
        )

        favoriteViewModel.state.test {
            favoriteViewModel.loadFavorites()

            // skip loading state
            skipItems(1)

            val successState = awaitItem()
            cancelAndIgnoreRemainingEvents()

            assertNotNull(successState)

            assertThat(successState).isInstanceOf<FavoriteScreenState.Content>()
            assertThat((successState as FavoriteScreenState.Content).diaries).isNotEmpty()
        }
    }

    @Test
    fun `Success state is emitted even when there is no favorite`() = runTest {
        every { dataSource.fetchFavorites() }.returns(flowOf(emptyList()))

        favoriteViewModel.state.test {
            favoriteViewModel.loadFavorites()

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
        coEvery { dataSource.update(any()) }.returns(1)

        favoriteViewModel.toggleFavorite(Diary("Hello"))

        coVerify { dataSource.update(any()) }
    }
}
