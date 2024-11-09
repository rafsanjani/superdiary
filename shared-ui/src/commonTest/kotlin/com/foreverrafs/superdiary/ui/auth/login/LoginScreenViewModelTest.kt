package com.foreverrafs.superdiary.ui.auth.login

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isInstanceOf
import com.foreverrafs.auth.AuthApi
import com.foreverrafs.auth.model.SessionInfo
import com.foreverrafs.superdiary.TestAppDispatchers
import com.foreverrafs.superdiary.ui.awaitUntil
import com.foreverrafs.superdiary.ui.feature.auth.login.LoginScreenViewModel
import com.foreverrafs.superdiary.ui.feature.auth.login.LoginViewState
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock

@OptIn(ExperimentalCoroutinesApi::class)
class LoginScreenViewModelTest {
    private lateinit var loginViewModel: LoginScreenViewModel
    private val authApi: FakeAuthApi = FakeAuthApi()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(TestAppDispatchers.main)

        loginViewModel = LoginScreenViewModel(
            authApi = authApi,
            coroutineDispatchers = TestAppDispatchers,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Should emit LoginViewState Processing with login flow begins`() = runTest {
        loginViewModel.viewState.test {
            loginViewModel.signInWithGoogle(null)
            val state = awaitUntil { it is LoginViewState.Processing }
            cancelAndIgnoreRemainingEvents()
            assertThat(state).isInstanceOf<LoginViewState.Processing>()
        }
    }

    @Test
    fun `Should emit LoginViewState Idle when login screen opens`() = runTest {
        loginViewModel.viewState.test {
            val state = awaitItem()
            expectNoEvents()
            assertThat(state).isInstanceOf<LoginViewState.Idle>()
        }
    }

    @Test
    fun `Should emit LoginViewState Error when signInWithGoogle fails`() = runTest {
        authApi.signInResult = AuthApi.SignInStatus.Error(Exception("Error logging in"))

        loginViewModel.viewState.test {
            loginViewModel.signInWithGoogle(null)
            val state = awaitUntil { it is LoginViewState.Error }
            expectNoEvents()
            assertThat(state).isInstanceOf<LoginViewState.Error>()
        }
    }

    @Test
    fun `Should emit LoginViewState Success when signInWithGoogle succeeds`() = runTest {
        authApi.signInResult = AuthApi.SignInStatus.LoggedIn(
            SessionInfo(
                expiresAt = Clock.System.now(),
                accessToken = "",
                refreshToken = "",
                userInfo = null,
            ),
        )

        loginViewModel.viewState.test {
            loginViewModel.signInWithGoogle(null)
            val state = awaitUntil { it is LoginViewState.Success }
            expectNoEvents()
            assertThat(state).isInstanceOf<LoginViewState.Success>()
        }
    }
}
