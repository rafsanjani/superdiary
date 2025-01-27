package com.foreverrafs.superdiary.auth.reset

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.foreverrafs.superdiary.auth.login.FakeAuthApi
import com.foreverrafs.superdiary.auth.reset.PasswordResetViewModel
import com.foreverrafs.superdiary.common.coroutines.TestAppDispatchers
import com.foreverrafs.superdiary.common.utils.AppCoroutineDispatchers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

class PasswordResetViewModelTest {

    private val authApi = FakeAuthApi()
    private val viewModel = PasswordResetViewModel(authApi)
    private val appDispatchers: AppCoroutineDispatchers = TestAppDispatchers

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun set() {
        Dispatchers.setMain(appDispatchers.main)
    }

    @Test
    fun `viewState initial state`() = runTest {
        viewModel.viewState.test {
            val state = awaitItem()

            assertThat(state.email).isEmpty()
            assertThat(state.isLoading).isFalse()
            assertThat(state.isEmailValid).isFalse()
            assertThat(state.inputErrorMessage).isNull()
            assertThat(state.isEmailSent).isNull()

            expectNoEvents()
        }
    }

    @Test
    fun `onResetPassword loading state`() = runTest {
        viewModel.viewState.test {
            val state = awaitItem()
            assertThat(state.isLoading).isFalse()

            viewModel.onResetPassword()
            val updatedState = awaitItem()
            cancelAndIgnoreRemainingEvents()
            assertThat(updatedState.isLoading).isTrue()
        }
    }

    @Test
    fun `onResetPassword email sent state`() = runTest {
        viewModel.viewState.test {
            val initialState = awaitItem()
            assertThat(initialState.isLoading).isFalse()

            viewModel.onResetPassword()
            val loadingState = awaitItem()

            assertThat(loadingState.isLoading).isTrue()

            val finalState = awaitItem()
            assertThat(finalState.isLoading).isFalse()
            expectNoEvents()
        }
    }

    @Test
    fun `onResetPassword email sending success`() = runTest {
        viewModel.viewState.test {
            val initialState = awaitItem()
            assertThat(initialState.isEmailSent).isNull()

            viewModel.onResetPassword()
            val loadingState = awaitItem()

            assertThat(loadingState.isEmailSent).isNull()

            val finalState = awaitItem()
            assertThat(finalState.isEmailSent).isNotNull()
            assertThat(finalState.isEmailSent).isEqualTo(true)
            expectNoEvents()
        }
    }

    @Test
    fun `onResetPassword authApi exception`() = runTest {
        authApi.sendPasswordResetEmailResult = Result.failure(Exception("Test exception"))

        viewModel.viewState.test {
            val initialState = awaitItem()
            assertThat(initialState.isEmailSent).isNull()

            viewModel.onResetPassword()

            val loadingState = awaitItem()
            assertThat(loadingState.isEmailSent).isNull()

            val finalState = awaitItem()
            assertThat(finalState.isEmailSent).isNotNull()
            assertThat(finalState.isEmailSent).isEqualTo(false)

            expectNoEvents()
        }
    }

    @Test
    fun `should show an error message when user inputs an invalid email`() = runTest {
        viewModel.onEmailChange("invalid_email")
        viewModel.viewState.test {
            val state = expectMostRecentItem()
            assertThat(state.isEmailValid).isFalse()
            assertThat(state.inputErrorMessage).isNotNull()
        }
    }

    @Test
    fun `should reset transient states when state is consumed`() = runTest {
        viewModel.onEmailChange("foreverrafs@gmail.com")
        // this will flip state.isEmailSent and other transient states to true
        viewModel.onResetPassword()
        // this will flip state.isEmailSent and other transient back to undefined
        viewModel.consumeTransientState()

        viewModel.viewState.test {
            val state = awaitItem()
            assertThat(state.isEmailSent).isNull()
        }
    }
}
