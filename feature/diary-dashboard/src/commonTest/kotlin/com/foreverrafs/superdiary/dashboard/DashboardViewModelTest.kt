package com.foreverrafs.superdiary.dashboard

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.foreverrafs.auth.BiometricAuth
import com.foreverrafs.preferences.DiaryPreference
import com.foreverrafs.superdiary.ai.api.DiaryAI
import com.foreverrafs.superdiary.common.coroutines.TestAppDispatchers
import com.foreverrafs.superdiary.common.coroutines.awaitUntil
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.dashboard.domain.CountEntriesUseCase
import com.foreverrafs.superdiary.dashboard.domain.GenerateWeeklySummaryUseCase
import com.foreverrafs.superdiary.dashboard.domain.GetRecentEntriesUseCase
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.data.datasource.InitialSyncState
import com.foreverrafs.superdiary.data.datasource.Syncable
import com.foreverrafs.superdiary.data.datasource.remote.DiaryApi
import com.foreverrafs.superdiary.data.model.DiaryDto
import com.foreverrafs.superdiary.database.Database
import com.foreverrafs.superdiary.database.testSuperDiaryDatabase
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.model.WeeklySummary
import com.foreverrafs.superdiary.domain.repository.DataSource
import com.foreverrafs.superdiary.domain.usecase.CalculateBestStreakUseCase
import com.foreverrafs.superdiary.domain.usecase.CalculateStreakUseCase
import com.foreverrafs.superdiary.domain.usecase.UpdateDiaryUseCase
import com.foreverrafs.superdiary.utils.DiarySettings
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalTime::class, ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {
    private lateinit var dataSource: DataSource
    private lateinit var syncableDataSource: DataSource

    private val diaryAI: DiaryAI = mock {
        every { generateSummary(any(), any()) } returns flowOf("summary")
    }

    private val diaryApi: DiaryApi = mock {
        everySuspend { countItems() } returns Result.Success(0)
        everySuspend { fetch(any()) } returns Result.Success(emptyList())
        every { fetchAll() } returns flowOf(listOf(DiaryDto("hello world")))
    }

    private val biometricAuth: BiometricAuth = mock {
        every { canAuthenticate() } returns true
        everySuspend { startBiometricAuth() } returns BiometricAuth.AuthResult.Success
    }

    private val diaryPreference = FakeDiaryPreference()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(TestAppDispatchers.main)
        dataSource = mock()
        syncableDataSource = FakeSyncableDataSource(dataSource)

        every { dataSource.fetchAll() }.returns(flowOf())
        every { dataSource.getLatest(any()) } returns flowOf(listOf(Diary("Hello World")))
        every { diaryApi.fetchAll() } returns flowOf()
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun createDashboardViewModel(
        diaryPreference: DiaryPreference = this.diaryPreference,
    ): DashboardViewModel = DashboardViewModel(
        calculateStreakUseCase = CalculateStreakUseCase(TestAppDispatchers),
        calculateBestStreakUseCase = CalculateBestStreakUseCase(TestAppDispatchers),
        updateDiaryUseCase = UpdateDiaryUseCase(dataSource, TestAppDispatchers),
        logger = AggregateLogger(emptyList()),
        preference = diaryPreference,
        biometricAuth = biometricAuth,
        generateWeeklySummaryUseCase = GenerateWeeklySummaryUseCase(
            logger = AggregateLogger(),
            diaryAI = diaryAI,
            clock = Clock.System,
            database = Database(testSuperDiaryDatabase),
        ),
        getRecentEntriesUseCase = GetRecentEntriesUseCase(syncableDataSource, AggregateLogger()),
        countEntriesUseCase = CountEntriesUseCase(diaryApi, AggregateLogger()),
    )

    @Test
    fun `Should load dashboard from loading state`() = runTest {
        val viewModel = createDashboardViewModel()
        viewModel.state.test {
            val state = awaitItem()

            assertThat(state).isInstanceOf<DashboardViewModel.DashboardScreenState.Loading>()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Should save settings when dashboard ordering is changed`() = runTest {
        val viewModel = createDashboardViewModel()

        viewModel.onUpdateSettings { DiarySettings.Empty }

        advanceUntilIdle()

        assertThat(diaryPreference.isSaveCalled).isTrue()
    }

    @Test
    fun `Should toggle favorite when favorite is toggled`() = runTest {
        val diary = Diary("Hello World")
        every { dataSource.fetchAll() }.returns(flowOf(listOf(diary)))
        everySuspend { dataSource.update(any()) }.returns(1)

        val viewModel = createDashboardViewModel()

        viewModel.toggleFavorite(diary)

        verifySuspend { dataSource.update(any()) }
    }

    @Test
    fun `Should fail to toggle favorite when datasource throws an error`() = runTest {
        val diary = Diary("Hello World")
        every { dataSource.fetchAll() }.returns(flowOf(listOf(diary)))
        everySuspend { dataSource.update(any()) }.throws(Exception("error toggling favorite"))

        val viewModel = createDashboardViewModel()

        val result = viewModel.toggleFavorite(diary)

        verifySuspend { dataSource.update(any()) }
        assertThat(result).isFalse()
    }

    private class FakeSyncableDataSource(
        private val delegate: DataSource,
    ) : DataSource by delegate, Syncable {
        override val initialSyncState: StateFlow<InitialSyncState> =
            MutableStateFlow(InitialSyncState.Completed)
    }

    @Test
    fun `Should transition to content state on dashboard after loading`() = runTest {
        every { dataSource.fetchAll() }.returns(flowOf(emptyList()))
        val viewModel = createDashboardViewModel()

        viewModel.state.test {
            skipItems(1)
            assertThat(awaitItem()).isInstanceOf(DashboardViewModel.DashboardScreenState.Content::class)
        }
    }

    @Test
    fun `Should show error screen when loading diaries throw an error`() = runTest {
        every { dataSource.getLatest(any()) } returns flowOf(emptyList())

        val viewModel = createDashboardViewModel()

        viewModel.state.test {
            val state =
                awaitUntil { it is DashboardViewModel.DashboardScreenState.Error } as DashboardViewModel.DashboardScreenState.Error

            assertThat(state.message).isNotEmpty()
        }
    }

    @Test
    fun `Should check if device supports biometric authentication when biometric auth is requested`() =
        runTest {
            val viewModel = createDashboardViewModel()
            viewModel.onEnableBiometricAuth()

            advanceUntilIdle()
            verify(mode = VerifyMode.exactly(1)) {
                biometricAuth.canAuthenticate()
            }
        }

    @Test
    fun `Should show biometric auth error when biometric auth fails`() = runTest {
        every { dataSource.fetchAll() }.returns(
            flowOf(
                listOf(Diary("Hello World")),
            ),
        )
        everySuspend { dataSource.getOne() } returns WeeklySummary("This is your weekly summary")

        everySuspend { biometricAuth.startBiometricAuth() } returns BiometricAuth.AuthResult.Error(
            Exception("failed"),
        )

        val viewModel = createDashboardViewModel()

        viewModel.state.test {
            skipItems(1)

            viewModel.onEnableBiometricAuth()
            val state = awaitItem()

            assertThat(state).isInstanceOf<DashboardViewModel.DashboardScreenState.Content>()
            val content = state as? DashboardViewModel.DashboardScreenState.Content

            assertThat(content?.isBiometricAuthError).isNotNull()
            assertThat(content?.isBiometricAuthError).isEqualTo(true)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Should attempt biometric authentication if option is enabled`() = runTest {
        every { dataSource.fetchAll() }.returns(
            flowOf(
                listOf(Diary("Hello World")),
            ),
        )
        everySuspend { dataSource.getOne() } returns WeeklySummary("This is your weekly summary")
        everySuspend { biometricAuth.startBiometricAuth() } returns BiometricAuth.AuthResult.Success
        everySuspend { biometricAuth.canAuthenticate() } returns true

        val viewModel = createDashboardViewModel()

        viewModel.state.test {
            // skip loading state
            skipItems(1)

            viewModel.onEnableBiometricAuth()
            val state = awaitItem()

            assertThat(state).isInstanceOf<DashboardViewModel.DashboardScreenState.Content>()
            val content = state as? DashboardViewModel.DashboardScreenState.Content

            assertThat(content?.isBiometricAuthError).isNull()
            assertThat(diaryPreference.isSaveCalled).isTrue()
        }
    }

    @Test
    fun `Should disable biometric authentication dialog when biometric auth is unavailable`() =
        runTest {
            every { dataSource.fetchAll() }.returns(
                flowOf(
                    listOf(Diary("Hello World")),
                ),
            )
            everySuspend { dataSource.getOne() } returns WeeklySummary("This is your weekly summary")

            everySuspend { biometricAuth.startBiometricAuth() } returns BiometricAuth.AuthResult.Failed
            every { biometricAuth.canAuthenticate() } returns false

            val viewModel = createDashboardViewModel()

            viewModel.state.test {
                skipItems(1)

                viewModel.onEnableBiometricAuth()
                val state = awaitItem()

                assertThat(state).isInstanceOf<DashboardViewModel.DashboardScreenState.Content>()
                val content = state as? DashboardViewModel.DashboardScreenState.Content

                assertThat(content?.isBiometricAuthError).isEqualTo(true)
                assertThat(content?.showBiometricAuthDialog).isEqualTo(false)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `Should display biometric dialog if biometric auth is available and dialog preference is true`() =
        runTest {
            every { dataSource.fetchAll() }.returns(
                flowOf(
                    listOf(Diary("Hello World")),
                ),
            )
            everySuspend { dataSource.getOne() } returns WeeklySummary("This is your weekly summary")
            everySuspend { biometricAuth.startBiometricAuth() } returns BiometricAuth.AuthResult.Success
            every { biometricAuth.canAuthenticate() } returns true

            diaryPreference.settingsResult =
                DiarySettings.Empty.copy(showBiometricAuthDialog = true)

            val viewModel = createDashboardViewModel()

            viewModel.state.test {
                skipItems(1)

                viewModel.onEnableBiometricAuth()
                val state = awaitItem()
                assertThat(state).isInstanceOf<DashboardViewModel.DashboardScreenState.Content>()

                val content = state as? DashboardViewModel.DashboardScreenState.Content

                assertThat(content?.showBiometricAuthDialog).isNotNull()
                assertThat(content?.showBiometricAuthDialog).isEqualTo(false)
            }
        }

    @Test
    fun `Should NOT display biometric dialog if biometric auth is available but dialog preference is false`() =
        runTest {
            every { dataSource.fetchAll() }.returns(
                flowOf(
                    listOf(Diary("Hello World")),
                ),
            )
            everySuspend { dataSource.getOne() } returns WeeklySummary("This is your weekly summary")

            everySuspend { biometricAuth.startBiometricAuth() } returns BiometricAuth.AuthResult.Success
            every { biometricAuth.canAuthenticate() } returns true

            diaryPreference.settingsResult =
                DiarySettings.Empty.copy(showBiometricAuthDialog = false)

            val viewModel = createDashboardViewModel()

            viewModel.state.test {
                skipItems(1)

                viewModel.onEnableBiometricAuth()
                val state = awaitItem()

                assertThat(state).isInstanceOf<DashboardViewModel.DashboardScreenState.Content>()
                val content = state as? DashboardViewModel.DashboardScreenState.Content

                assertThat(content?.isBiometricAuthError).isNull()
                assertThat(content?.showBiometricAuthDialog).isEqualTo(false)
            }
        }
}
