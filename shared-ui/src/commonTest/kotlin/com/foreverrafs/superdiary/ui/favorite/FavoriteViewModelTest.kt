package com.foreverrafs.superdiary.ui.favorite

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotEmpty
import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.data.usecase.GetFavoriteDiariesUseCase
import com.foreverrafs.superdiary.ui.feature.favorites.model.FavoriteViewModel
import com.foreverrafs.superdiary.ui.feature.favorites.screen.FavoriteScreenState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import org.kodein.mock.Mock
import org.kodein.mock.tests.TestsWithMocks
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteViewModelTest : TestsWithMocks() {
    override fun setUpMocks() = injectMocks(mocker)

    @Mock
    lateinit var dataSource: DataSource

    private lateinit var favoriteViewModel: FavoriteViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())

        favoriteViewModel = FavoriteViewModel(
            getFavoriteDiariesUseCase = GetFavoriteDiariesUseCase(dataSource),
        )
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Verify favorite screen starts from loading state`() = runTest {
        every { dataSource.fetchFavorites() } returns flowOf(
            listOf(
                Diary(
                    entry = "Fake Diary",
                    date = Clock.System.now(),
                    isFavorite = true,
                ),
            ),
        )

        favoriteViewModel.state.test {
            favoriteViewModel.loadFavorites()
            val loadingState = awaitItem()
            cancelAndIgnoreRemainingEvents()

            assertThat(loadingState).isInstanceOf<FavoriteScreenState.Loading>()
        }
    }

    @Test
    fun `Verify success state is emitted after loading favorites`() = runTest {
        every { dataSource.fetchFavorites() } returns flowOf(
            listOf(
                Diary(
                    entry = "Fake Diary",
                    date = Clock.System.now(),
                    isFavorite = true,
                ),
            ),
        )

        favoriteViewModel.state.test {
            favoriteViewModel.loadFavorites()

            // skip loading state
            skipItems(1)

            val successState = awaitItem()
            cancelAndIgnoreRemainingEvents()

            assertThat(successState).isInstanceOf<FavoriteScreenState.Favorites>()
            assertThat((successState as FavoriteScreenState.Favorites).diaries).isNotEmpty()
        }
    }

    @Test
    fun `Verify success state is emitted even when there is no favorite`() = runTest {
        every { dataSource.fetchFavorites() } returns flowOf(emptyList())

        favoriteViewModel.state.test {
            favoriteViewModel.loadFavorites()

            // skip loading state
            skipItems(1)

            val successState = awaitItem()
            cancelAndIgnoreRemainingEvents()

            assertThat(successState).isInstanceOf<FavoriteScreenState.Favorites>()
            assertThat((successState as FavoriteScreenState.Favorites).diaries).isEmpty()
        }
    }
}
