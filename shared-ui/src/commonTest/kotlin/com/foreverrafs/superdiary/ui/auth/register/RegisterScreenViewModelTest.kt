package com.foreverrafs.superdiary.ui.auth.register

import app.cash.turbine.test
import assertk.assertThat
import com.foreverrafs.auth.AuthApi
import com.foreverrafs.superdiary.TestAppDispatchers
import com.foreverrafs.superdiary.core.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.ui.auth.login.FakeAuthApi
import com.foreverrafs.superdiary.ui.feature.auth.register.RegisterScreenViewModel
import com.foreverrafs.superdiary.ui.feature.auth.register.screen.RegisterScreenState
import io.ktor.util.reflect.instanceOf
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

class RegisterScreenViewModelTest {

    private val authApi = FakeAuthApi()
    private val appDispatchers: AppCoroutineDispatchers = TestAppDispatchers
    private val viewModel = RegisterScreenViewModel(authApi, appDispatchers)

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun set() {
        Dispatchers.setMain(appDispatchers.main)
    }

    @Test
    fun `viewState initial state`() = runTest {
        viewModel.viewState.test {
            val state = awaitItem()

            assertThat(state).instanceOf(RegisterScreenState.Idle::class)

            expectNoEvents()
        }
    }

    @Test
    fun `Should emit success state after signing up with email`() = runTest {
        authApi.registerWithEmailResult = AuthApi.RegistrationStatus.Success

        viewModel.viewState.test {
            // Skip initial Idle state
            skipItems(1)

            viewModel.onRegisterClick(
                name = "John Doe",
                email = "john@doe.com",
                password = "weak_password",
            )
            // skip processing state
            awaitItem()
            assertThat(awaitItem()).instanceOf(RegisterScreenState.Success::class)

            expectNoEvents()
        }
    }

    @Test
    fun `Should emit failure state when email registration fails`() = runTest {
        authApi.registerWithEmailResult =
            AuthApi.RegistrationStatus.Error(Exception("error registering"))

        viewModel.viewState.test {
            // Skip initial Idle state
            skipItems(1)

            viewModel.onRegisterClick(
                name = "John Doe",
                email = "john@doe.com",
                password = "weak_password",
            )

            // skip processing state
            skipItems(1)
            assertThat(awaitItem()).instanceOf(RegisterScreenState.Error::class)

            expectNoEvents()
        }
    }
}
