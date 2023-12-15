package com.foreverrafs.superdiary.ui.favorite

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotEmpty
import com.foreverrafs.superdiary.TestDataSource
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.usecase.GetFavoriteDiariesUseCase
import com.foreverrafs.superdiary.ui.feature.favorites.model.FavoriteScreenModel
import com.foreverrafs.superdiary.ui.feature.favorites.screen.FavoriteScreenState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteScreenModelTest {
    private lateinit var dataSource: DataSource
    private lateinit var favoriteScreenModel: FavoriteScreenModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        dataSource = TestDataSource()
        favoriteScreenModel = FavoriteScreenModel(GetFavoriteDiariesUseCase(dataSource))
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Verify favorite screen starts from loading state`() = runTest {
        favoriteScreenModel.state.test {
            favoriteScreenModel.loadFavorites()
            val loadingState = awaitItem()
            cancelAndIgnoreRemainingEvents()

            assertThat(loadingState).isInstanceOf<FavoriteScreenState.Loading>()
        }
    }

    @Test
    fun `Verify success state is emitted after loading favorites`() = runTest {
        val favoritePresentDataSource = object : TestDataSource() {
            override fun fetchFavorites(): Flow<List<Diary>> {
                return flowOf(
                    listOf(
                        Diary(
                            entry = "",
                            date = Clock.System.now(),
                            isFavorite = true,
                        ),
                    ),
                )
            }
        }
        favoriteScreenModel =
            FavoriteScreenModel(GetFavoriteDiariesUseCase(favoritePresentDataSource))

        favoriteScreenModel.state.test {
            favoriteScreenModel.loadFavorites()

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
        val noFavoriteDataSource = object : TestDataSource() {
            override fun fetchFavorites(): Flow<List<Diary>> {
                return flowOf(emptyList())
            }
        }
        favoriteScreenModel = FavoriteScreenModel(GetFavoriteDiariesUseCase(noFavoriteDataSource))

        favoriteScreenModel.state.test {
            favoriteScreenModel.loadFavorites()

            // skip loading state
            skipItems(1)

            val successState = awaitItem()
            cancelAndIgnoreRemainingEvents()

            assertThat(successState).isInstanceOf<FavoriteScreenState.Favorites>()
            assertThat((successState as FavoriteScreenState.Favorites).diaries).isEmpty()
        }
    }
}
