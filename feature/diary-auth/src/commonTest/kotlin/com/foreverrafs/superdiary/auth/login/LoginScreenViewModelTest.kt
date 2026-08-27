package com.foreverrafs.superdiary.auth.login

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.foreverrafs.auth.AuthApi
import com.foreverrafs.auth.AuthUiContext
import com.foreverrafs.auth.model.SessionInfo
import com.foreverrafs.auth.model.UserInfo
import com.foreverrafs.superdiary.auth.analytics.AuthAnalyticsEvent
import com.foreverrafs.superdiary.auth.login.screen.LoginViewState
import com.foreverrafs.superdiary.common.coroutines.TestAppDispatchers
import com.foreverrafs.superdiary.common.coroutines.awaitUntil
import com.foreverrafs.superdiary.core.analytics.AnalyticsEvent
import com.foreverrafs.superdiary.core.analytics.AnalyticsTracker
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Clock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class LoginScreenViewModelTest {
    private val authUiContext = object : AuthUiContext {}
    private lateinit var loginViewModel: LoginScreenViewModel
    private val trackedEvents = mutableListOf<AnalyticsEvent>()

    private val authApi: FakeAuthApi = FakeAuthApi()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(TestAppDispatchers.main)
        trackedEvents.clear()

        loginViewModel = LoginScreenViewModel(
            authApi = authApi,
            coroutineDispatchers = TestAppDispatchers,
            analyticsTracker = AnalyticsTracker(trackedEvents::add),
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Should emit LoginViewState Processing when signInWithGoogle flow begins`() = runTest {
        loginViewModel.viewState.test {
            loginViewModel.onLoginWithGoogle(authUiContext)
            val state = awaitUntil { it is LoginViewState.Processing }
            cancelAndIgnoreRemainingEvents()
            assertThat(state).isInstanceOf<LoginViewState.Processing>()
        }
    }

    @Test
    fun `Should emit LoginViewState Processing when email login flow begins`() = runTest {
        loginViewModel.viewState.test {
            loginViewModel.onLoginWithEmail("user", "pass")
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
        authApi.signInResult = AuthApi.SessionStatus.Unauthenticated(Exception("Error logging in"))

        loginViewModel.viewState.test {
            loginViewModel.onLoginWithGoogle(authUiContext)
            val state = awaitUntil { it is LoginViewState.Error }
            expectNoEvents()
            assertThat(state).isInstanceOf<LoginViewState.Error>()
        }
    }

    @Test
    fun `Should emit LoginViewState Error when signInWithGoogle throws`() = runTest {
        authApi.googleSignInException = Exception("No credentials available")

        loginViewModel.viewState.test {
            loginViewModel.onLoginWithGoogle(authUiContext)
            val state = awaitUntil { it is LoginViewState.Error }
            expectNoEvents()
            assertThat(state).isInstanceOf<LoginViewState.Error>()
        }
    }

    @Test
    fun `Should emit LoginViewState Error when email login fails`() = runTest {
        authApi.signInResult = AuthApi.SessionStatus.Unauthenticated(Exception("Error logging in"))

        loginViewModel.viewState.test {
            loginViewModel.onLoginWithEmail("email", "pass")
            val state = awaitUntil { it is LoginViewState.Error }
            expectNoEvents()
            assertThat(state).isInstanceOf<LoginViewState.Error>()
        }
    }

    @Test
    fun `Should emit LoginViewState Success when signInWithGoogle succeeds`() = runTest {
        authApi.signInResult = AuthApi.SessionStatus.Authenticated(
            SessionInfo(
                expiresAt = Clock.System.now(),
                accessToken = "",
                refreshToken = "",
                userInfo = UserInfo(
                    id = "",
                    avatarUrl = "",
                    name = "",
                    email = "",
                ),
            ),
        )

        loginViewModel.viewState.test {
            loginViewModel.onLoginWithGoogle(authUiContext)
            val state = awaitUntil { it is LoginViewState.Success }
            expectNoEvents()
            assertThat(state).isInstanceOf<LoginViewState.Success>()
        }

        assertThat(trackedEvents).isEqualTo(
            listOf(AuthAnalyticsEvent.Login(method = "google")),
        )
    }
}
