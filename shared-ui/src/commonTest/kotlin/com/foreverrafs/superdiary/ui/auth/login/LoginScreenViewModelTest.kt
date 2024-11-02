package com.foreverrafs.superdiary.ui.auth.login

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.foreverrafs.auth.AuthApi
import com.foreverrafs.superdiary.TestAppDispatchers
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.utils.DiaryPreference
import com.foreverrafs.superdiary.data.utils.DiaryPreferenceImpl
import com.foreverrafs.superdiary.ui.awaitUntil
import com.foreverrafs.superdiary.ui.feature.auth.login.LoginScreenViewModel
import com.foreverrafs.superdiary.ui.feature.auth.login.LoginViewState
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

class LoginScreenViewModelTest {
    private lateinit var loginViewModel: LoginScreenViewModel

    private val authApi: AuthApi = mock()

    private val diaryPreference: DiaryPreference = DiaryPreferenceImpl.getInstance(
        dispatchers = TestAppDispatchers,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(TestAppDispatchers.main)

        loginViewModel = LoginScreenViewModel(
            authApi = authApi,
            coroutineDispatchers = TestAppDispatchers,
            diaryPreference = diaryPreference,
            logger = AggregateLogger(emptyList()),
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Should attempt to login when a session token is available`() = runTest {
        val token = "sample-auth-token"
        savePreviousSessionToken(token)

        everySuspend {
            authApi.signInWithGoogle(token)
        }.returns(
            AuthApi.SignInStatus.LoggedIn(
                token,
            ),
        )

        loginViewModel.viewState.test {
            val state = awaitUntil { it is LoginViewState.Processing }

            assertThat(state).isInstanceOf(LoginViewState.Processing::class)
        }
    }

    @Test
    fun `Should NOT attempt to login when a session token is NOT available`() = runTest {
        // Save an empty token which is analogous to no token being stored
        savePreviousSessionToken("")

        loginViewModel.viewState.test {
            val state = awaitItem()

            assertThat(state).isInstanceOf(LoginViewState.Idle::class)
            expectNoEvents()
        }
    }

    @Test
    fun `Should save login token after successful login`() = runTest {
        val token = "old-auth-session-token"
        savePreviousSessionToken(token)

        everySuspend {
            authApi.signInWithGoogle(token)
        }.returns(
            AuthApi.SignInStatus.LoggedIn(token = "new-session-token"),
        )

        loginViewModel.viewState.test {
            awaitUntil { it is LoginViewState.Success }

            val preference = diaryPreference.snapshot

            assertThat(preference.authorizationToken).isEqualTo("new-session-token")
        }
    }

    @Test
    fun `Should login successfully when a session token is available`() = runTest {
        val token = "old-auth-session-token"
        savePreviousSessionToken(token)

        everySuspend {
            authApi.signInWithGoogle(token)
        }.returns(
            AuthApi.SignInStatus.LoggedIn(token = "new-session-token"),
        )

        loginViewModel.viewState.test {
            val state = awaitUntil { it is LoginViewState.Success }
            assertThat(state).isInstanceOf(LoginViewState.Success::class)
        }
    }

    @Test
    fun `Should emit LoginViewState Error when signInWithGoogle fails`() = runTest {
        val token = "old-auth-session-token"
        savePreviousSessionToken(token)

        everySuspend {
            authApi.signInWithGoogle(token)
        }.returns(
            AuthApi.SignInStatus.Error(Exception("custom-error-occurred")),
        )

        loginViewModel.viewState.test {
            val state = awaitUntil { it is LoginViewState.Error }
            assertThat(state).isInstanceOf(LoginViewState.Error::class)
        }
    }

    /**
     * Stores the previous session token in [DiaryPreference]. This enables
     * automatic session restoration for a smoother user experience, or prompts
     * for re-login if necessary.
     *
     * @param token The previous session token to store.
     */
    private suspend fun savePreviousSessionToken(token: String) {
        val snapshot = diaryPreference.snapshot
        diaryPreference.save(
            snapshot.copy(
                authorizationToken = token,
            ),
        )
    }
}
