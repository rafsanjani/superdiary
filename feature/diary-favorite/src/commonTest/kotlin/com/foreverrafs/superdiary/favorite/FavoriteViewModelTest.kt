package com.foreverrafs.superdiary.favorite

import androidx.paging.PagingData
import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import com.foreverrafs.superdiary.common.coroutines.TestAppDispatchers
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.repository.DataSource
import com.foreverrafs.superdiary.domain.usecase.GetFavoriteDiariesUseCase
import com.foreverrafs.superdiary.domain.usecase.UpdateDiaryUseCase
import com.foreverrafs.superdiary.favorite.screen.FavoriteScreenState
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
class FavoriteViewModelTest {
    private val dataSource: DataSource = mock<DataSource>()

    private lateinit var favoriteViewModel: FavoriteViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        every { dataSource.fetchFavoritesPaged() }.returns(flowOf(PagingData.empty()))

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
            val successState = awaitContentState()
            cancelAndIgnoreRemainingEvents()

            assertNotNull(successState)

            assertThat(successState).isInstanceOf<FavoriteScreenState.Content>()
        }
    }

    @Test
    fun `Success state is emitted even when there is no favorite`() = runTest {
        every { dataSource.fetchFavoritesPaged() }.returns(flowOf(PagingData.empty()))

        favoriteViewModel.state.test {
            val successState = awaitContentState()
            cancelAndIgnoreRemainingEvents()
            assertNotNull(successState)

            assertThat(successState).isInstanceOf<FavoriteScreenState.Content>()
        }
    }

    @Test
    fun `Should fail toggle favorite when an error occurs during update`() = runTest {
        everySuspend { dataSource.update(any()) }.throws(Exception("Error fetching favorites"))

        val result = favoriteViewModel.toggleFavorite(Diary("Hello"))
        assertThat(result).isFalse()
    }

    @Test
    fun `Removing favorite actually removes it`() = runTest {
        everySuspend { dataSource.update(any()) }.returns(1)

        favoriteViewModel.toggleFavorite(Diary("Hello"))

        verifySuspend { dataSource.update(any()) }
    }

    private suspend fun ReceiveTurbine<FavoriteScreenState?>.awaitContentState(): FavoriteScreenState {
        val state = awaitItem()
        return if (state is FavoriteScreenState.Content) {
            state
        } else {
            awaitItem() ?: error("Expected favorite content state")
        }
    }
}
