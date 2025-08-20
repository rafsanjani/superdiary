package com.foreverrafs.superdiary.dashboard

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.foreverrafs.auth.BiometricAuth
import com.foreverrafs.preferences.DiaryPreference
import com.foreverrafs.superdiary.ai.api.DiaryAI
import com.foreverrafs.superdiary.common.coroutines.TestAppDispatchers
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.model.WeeklySummary
import com.foreverrafs.superdiary.domain.repository.DataSource
import com.foreverrafs.superdiary.domain.usecase.AddWeeklySummaryUseCase
import com.foreverrafs.superdiary.domain.usecase.CalculateBestStreakUseCase
import com.foreverrafs.superdiary.domain.usecase.CalculateStreakUseCase
import com.foreverrafs.superdiary.domain.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.domain.usecase.GetWeeklySummaryUseCase
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus

@OptIn(ExperimentalTime::class)
class DashboardViewModelTest {
    private lateinit var dataSource: DataSource

    private val diaryAI: DiaryAI = mock()

    private val biometricAuth: BiometricAuth = mock {
        everySuspend { canAuthenticate() } returns true
        everySuspend { startBiometricAuth() } returns BiometricAuth.AuthResult.Success
    }

    private val diaryPreference = FakeDiaryPreference()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(TestAppDispatchers.main)
        dataSource = mock()

        every { dataSource.fetchAll() }.returns(flowOf())
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun createDashboardViewModel(
        diaryPreference: DiaryPreference = this.diaryPreference,
    ): DashboardViewModel = DashboardViewModel(
        getAllDiariesUseCase = GetAllDiariesUseCase(dataSource, TestAppDispatchers),
        calculateStreakUseCase = CalculateStreakUseCase(TestAppDispatchers),
        addWeeklySummaryUseCase = AddWeeklySummaryUseCase(dataSource, TestAppDispatchers),
        getWeeklySummaryUseCase = GetWeeklySummaryUseCase(dataSource, TestAppDispatchers),
        diaryAI = diaryAI,
        calculateBestStreakUseCase = CalculateBestStreakUseCase(TestAppDispatchers),
        updateDiaryUseCase = UpdateDiaryUseCase(dataSource, TestAppDispatchers),
        logger = AggregateLogger(emptyList()),
        preference = diaryPreference,
        clock = Clock.System,
        biometricAuth = biometricAuth,
    )

    @Test
    fun `Should load dashboard from loading state`() = runTest {
        val viewModel = createDashboardViewModel()
        viewModel.state.test {
            val state = awaitItem()

            assertThat(state).isInstanceOf<DashboardViewModel.DashboardScreenState.Loading>()
        }
    }

    @Test
    fun `Should generate weekly summary if diaries isn't empty`() = runTest {
        every { dataSource.fetchAll() }.returns(
            flowOf(
                listOf(Diary("Hello World")),
            ),
        )
        everySuspend { dataSource.getWeeklySummary() }.returns(
            WeeklySummary("This is your weekly summary"),
        )

        val viewModel = createDashboardViewModel()

        viewModel.state.test {
            skipItems(2)
            val state = awaitItem()
            cancelAndIgnoreRemainingEvents()

            assertThat(state).isInstanceOf<DashboardViewModel.DashboardScreenState.Content>()
            val content: DashboardViewModel.DashboardScreenState.Content =
                state as DashboardViewModel.DashboardScreenState.Content

            assertThat(content.weeklySummary!!).contains("This is your weekly summary")
        }
    }

    @Test
    fun `Should only generate weekly summary every week`() = runTest {
        every { dataSource.fetchAll() }.returns(
            flowOf(listOf(Diary("Hello World"))),
        )

        every { dataSource.getWeeklySummary() }.returns(
            WeeklySummary(
                summary = "Old diary summary",
                date = Clock.System.now().minus(
                    value = 5,
                    unit = DateTimeUnit.DAY,
                    timeZone = TimeZone.UTC,
                ),
            ),
        )
        every { diaryAI.generateSummary(any()) }.returns(flowOf("New Diary Summary"))

        val viewModel = createDashboardViewModel()

        viewModel.state.test {
            // Skip the loading state
            skipItems(2)
            val state = awaitItem() as? DashboardViewModel.DashboardScreenState.Content

            cancelAndIgnoreRemainingEvents()
            assertThat(state).isNotNull()
            assertThat(state?.weeklySummary).isEqualTo("Old diary summary")
        }
    }

    @Test
    fun `Should generate weekly summary when weekly summary is older than a week`() = runTest {
        every { dataSource.fetchAll() }.returns(flowOf(listOf(Diary("Hello World"))))
        everySuspend { dataSource.insertWeeklySummary(any()) }.returns(Unit)
        every { dataSource.getWeeklySummary() }.returns(
            WeeklySummary(
                summary = "Old diary summary",
                date = Clock.System.now().minus(value = 20, unit = DateTimeUnit.DAY, TimeZone.UTC),
            ),
        )
        every { diaryAI.generateSummary(any()) }.returns(flowOf("New Diary Summary"))

        val viewModel = createDashboardViewModel()

        viewModel.state.test {
            // Skip the loading state
            skipItems(2)
            val state = awaitItem() as? DashboardViewModel.DashboardScreenState.Content

            cancelAndIgnoreRemainingEvents()
            assertThat(state).isNotNull()
            assertThat(state?.weeklySummary).isEqualTo("New Diary Summary")
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
        every { dataSource.fetchAll() }.throws(Exception("Error fetching diaries"))
        val viewModel = createDashboardViewModel()

        viewModel.state.test {
            skipItems(1)
            assertThat(awaitItem()).isInstanceOf(DashboardViewModel.DashboardScreenState.Error::class)
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
        everySuspend { dataSource.getWeeklySummary() } returns WeeklySummary("This is your weekly summary")

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
        }
    }

    @Test
    fun `Should enable biometric authentication when biometric auth succeeds`() = runTest {
        every { dataSource.fetchAll() }.returns(
            flowOf(
                listOf(Diary("Hello World")),
            ),
        )
        everySuspend { dataSource.getWeeklySummary() } returns WeeklySummary("This is your weekly summary")

        everySuspend { biometricAuth.startBiometricAuth() } returns BiometricAuth.AuthResult.Success

        val viewModel = createDashboardViewModel()

        viewModel.state.test {
            skipItems(2)

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
            everySuspend { dataSource.getWeeklySummary() } returns WeeklySummary("This is your weekly summary")

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
            everySuspend { dataSource.getWeeklySummary() } returns WeeklySummary("This is your weekly summary")

            everySuspend { biometricAuth.startBiometricAuth() } returns BiometricAuth.AuthResult.Success
            every { biometricAuth.canAuthenticate() } returns true
            diaryPreference.settingsResult =
                DiarySettings.Empty.copy(showBiometricAuthDialog = true)

            val viewModel = createDashboardViewModel()

            viewModel.state.test {
                skipItems(2)

                viewModel.onEnableBiometricAuth()
                val state = awaitItem()

                assertThat(state).isInstanceOf<DashboardViewModel.DashboardScreenState.Content>()
                val content = state as? DashboardViewModel.DashboardScreenState.Content

                assertThat(content?.isBiometricAuthError).isNull()
                assertThat(content?.showBiometricAuthDialog).isEqualTo(true)
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
            everySuspend { dataSource.getWeeklySummary() } returns WeeklySummary("This is your weekly summary")

            everySuspend { biometricAuth.startBiometricAuth() } returns BiometricAuth.AuthResult.Success
            every { biometricAuth.canAuthenticate() } returns true

            diaryPreference.settingsResult =
                DiarySettings.Empty.copy(showBiometricAuthDialog = false)

            val viewModel = createDashboardViewModel()

            viewModel.state.test {
                skipItems(2)

                viewModel.onEnableBiometricAuth()
                val state = awaitItem()

                assertThat(state).isInstanceOf<DashboardViewModel.DashboardScreenState.Content>()
                val content = state as? DashboardViewModel.DashboardScreenState.Content

                assertThat(content?.isBiometricAuthError).isNull()
                assertThat(content?.showBiometricAuthDialog).isEqualTo(false)
            }
        }
}
