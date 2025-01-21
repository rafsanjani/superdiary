package com.foreverrafs.superdiary.ui.auth.login

import app.cash.turbine.test
import assertk.assertThat
import com.foreverrafs.auth.BiometricAuth
import com.foreverrafs.superdiary.common.coroutines.TestAppDispatchers
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.ui.feature.auth.login.BiometricAuthUnavailableException
import com.foreverrafs.superdiary.ui.feature.auth.login.BiometricAuthenticationException
import com.foreverrafs.superdiary.ui.feature.auth.login.BiometricLoginScreenState
import com.foreverrafs.superdiary.ui.feature.auth.login.BiometricLoginScreenViewModel
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import io.ktor.util.reflect.instanceOf
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class BiometricLoginScreenViewModelTest {
    private lateinit var loginViewModel: BiometricLoginScreenViewModel
    private val biometricAuth: BiometricAuth = mock()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(TestAppDispatchers.main)

        loginViewModel = BiometricLoginScreenViewModel(
            biometricAuth = biometricAuth,
            logger = AggregateLogger(emptyList()),
            coroutineDispatchers = TestAppDispatchers,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Should attempt biometric auth immediately auth screen is opened`() = runTest {
        every { biometricAuth.canAuthenticate() } returns true
        everySuspend { biometricAuth.startBiometricAuth() } returns BiometricAuth.AuthResult.Success

        loginViewModel.viewState.test {
            advanceUntilIdle()
            cancelAndIgnoreRemainingEvents()
            verifySuspend { biometricAuth.startBiometricAuth() }
        }
    }

    @Test
    fun `Should show error screen if biometric authentication is not possible on device`() =
        runTest {
            every { biometricAuth.canAuthenticate() } returns false
            everySuspend { biometricAuth.startBiometricAuth() } returns BiometricAuth.AuthResult.Failed

            loginViewModel.viewState.test {
                advanceUntilIdle()
                val state = expectMostRecentItem()

                assertThat(state).instanceOf(BiometricLoginScreenState.Error::class)
                assertThat((state as? BiometricLoginScreenState.Error)?.exception).instanceOf(
                    BiometricAuthUnavailableException::class,
                )
            }
        }

    @Test
    fun `Should show error screen if biometric authentication fails`() =
        runTest {
            every { biometricAuth.canAuthenticate() } returns true
            everySuspend { biometricAuth.startBiometricAuth() } returns BiometricAuth.AuthResult.Failed

            loginViewModel.viewState.test {
                advanceUntilIdle()
                val state = expectMostRecentItem()

                assertThat(state).instanceOf(BiometricLoginScreenState.Error::class)
                assertThat((state as? BiometricLoginScreenState.Error)?.exception).instanceOf(
                    BiometricAuthenticationException::class,
                )
            }
        }

    @Test
    fun `Should return success state when biometric authentication succeeds`() =
        runTest {
            every { biometricAuth.canAuthenticate() } returns true
            everySuspend { biometricAuth.startBiometricAuth() } returns BiometricAuth.AuthResult.Success

            loginViewModel.viewState.test {
                advanceUntilIdle()
                val state = expectMostRecentItem()

                assertThat(state).instanceOf(BiometricLoginScreenState.Success::class)
            }
        }
}
