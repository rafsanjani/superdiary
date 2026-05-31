package com.foreverrafs.superdiary.auth.register

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import com.foreverrafs.auth.AuthApi
import com.foreverrafs.superdiary.auth.login.FakeAuthApi
import com.foreverrafs.superdiary.auth.register.screen.RegisterScreenState
import com.foreverrafs.superdiary.common.coroutines.TestAppDispatchers
import com.foreverrafs.superdiary.common.utils.AppCoroutineDispatchers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

class RegisterScreenViewModelTest {

    private val authApi = FakeAuthApi()
    private val appDispatchers: AppCoroutineDispatchers = TestAppDispatchers
    private val viewModel = RegisterScreenViewModel(authApi, appDispatchers, RegistrationFormValidator())

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun set() {
        Dispatchers.setMain(appDispatchers.main)
    }

    @Test
    fun `viewState initial state`() = runTest {
        viewModel.viewState.test {
            val state = awaitItem()

            assertThat(state).isInstanceOf(RegisterScreenState.Idle::class)

            expectNoEvents()
        }
    }

    @Test
    fun `Should transition through Processing to Success when registration succeeds`() = runTest {
        authApi.registerWithEmailResult = AuthApi.RegistrationStatus.Success

        viewModel.viewState.test {
            // Skip initial Idle state
            skipItems(1)

            viewModel.onRegisterClick(
                name = "John Doe",
                email = "john@doe.com",
                password = "weak_password",
                verifyPassword = "weak_password",
            )

            // Processing
            assertThat(awaitItem()).isInstanceOf(RegisterScreenState.Processing::class)

            // Success
            assertThat(awaitItem()).isInstanceOf(RegisterScreenState.Success::class)

            expectNoEvents()
        }
    }

    @Test
    fun `Should emit Error when registration fails`() = runTest {
        authApi.registerWithEmailResult =
            AuthApi.RegistrationStatus.Error(Exception("error registering"))

        viewModel.viewState.test {
            // Skip initial Idle state
            skipItems(1)

            viewModel.onRegisterClick(
                name = "John Doe",
                email = "john@doe.com",
                password = "weak_password",
                verifyPassword = "weak_password",
            )

            // Processing
            assertThat(awaitItem()).isInstanceOf(RegisterScreenState.Processing::class)

            // Error
            assertThat(awaitItem()).isInstanceOf(RegisterScreenState.Error::class)

            expectNoEvents()
        }
    }

    @Test
    fun `Should emit ValidationError when name is empty`() = runTest {
        viewModel.viewState.test {
            // Skip initial Idle state
            skipItems(1)

            viewModel.onRegisterClick(
                name = "",
                email = "john@doe.com",
                password = "weak_password",
                verifyPassword = "weak_password",
            )

            val state = awaitItem()
            assertThat(state).isInstanceOf(RegisterScreenState.ValidationError::class)

            val errors = (state as RegisterScreenState.ValidationError).errors
            assertThat(errors.nameError).isEqualTo(FieldValidationError.Required)
            assertThat(errors.emailError).isNull()
            assertThat(errors.passwordError).isNull()
            assertThat(errors.verifyPasswordError).isNull()

            expectNoEvents()
        }
    }

    @Test
    fun `Should emit ValidationError when email is invalid`() = runTest {
        viewModel.viewState.test {
            // Skip initial Idle state
            skipItems(1)

            viewModel.onRegisterClick(
                name = "John Doe",
                email = "not-an-email",
                password = "weak_password",
                verifyPassword = "weak_password",
            )

            val state = awaitItem()
            assertThat(state).isInstanceOf(RegisterScreenState.ValidationError::class)

            val errors = (state as RegisterScreenState.ValidationError).errors
            assertThat(errors.emailError).isEqualTo(FieldValidationError.InvalidEmail)

            // No API call was made — viewModel should not have emitted Processing
            expectNoEvents()
        }
    }

    @Test
    fun `Should emit ValidationError when passwords do not match`() = runTest {
        viewModel.viewState.test {
            // Skip initial Idle state
            skipItems(1)

            viewModel.onRegisterClick(
                name = "John Doe",
                email = "john@doe.com",
                password = "secret123",
                verifyPassword = "different",
            )

            val state = awaitItem()
            assertThat(state).isInstanceOf(RegisterScreenState.ValidationError::class)

            val errors = (state as RegisterScreenState.ValidationError).errors
            assertThat(errors.verifyPasswordError).isEqualTo(FieldValidationError.PasswordsDoNotMatch)

            expectNoEvents()
        }
    }

    @Test
    fun `Should emit all validation errors when the entire form is empty`() = runTest {
        viewModel.viewState.test {
            // Skip initial Idle state
            skipItems(1)

            viewModel.onRegisterClick(
                name = "",
                email = "",
                password = "",
                verifyPassword = "",
            )

            val state = awaitItem()
            assertThat(state).isInstanceOf(RegisterScreenState.ValidationError::class)

            val errors = (state as RegisterScreenState.ValidationError).errors
            assertThat(errors.nameError).isEqualTo(FieldValidationError.Required)
            assertThat(errors.emailError).isEqualTo(FieldValidationError.Required)
            assertThat(errors.passwordError).isEqualTo(FieldValidationError.Required)
            assertThat(errors.verifyPasswordError).isEqualTo(FieldValidationError.Required)

            expectNoEvents()
        }
    }

    @Test
    fun `Should clear validation error state when onFieldChanged is called`() = runTest {
        viewModel.viewState.test {
            // Skip initial Idle state
            skipItems(1)

            // Trigger validation failure
            viewModel.onRegisterClick(
                name = "",
                email = "john@doe.com",
                password = "weak_password",
                verifyPassword = "weak_password",
            )

            assertThat(awaitItem()).isInstanceOf(RegisterScreenState.ValidationError::class)

            // Simulate user typing
            viewModel.onFieldChanged()

            assertThat(awaitItem()).isInstanceOf(RegisterScreenState.Idle::class)

            expectNoEvents()
        }
    }

    @Test
    fun `onFieldChanged should be a no-op when current state is not ValidationError`() = runTest {
        viewModel.viewState.test {
            assertThat(awaitItem()).isInstanceOf(RegisterScreenState.Idle::class)

            // Should not emit any new state
            viewModel.onFieldChanged()
            expectNoEvents()
        }
    }
}
