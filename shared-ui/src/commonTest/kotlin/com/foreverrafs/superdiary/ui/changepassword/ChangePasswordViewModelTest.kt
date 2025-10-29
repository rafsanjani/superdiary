package com.foreverrafs.superdiary.ui.changepassword

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.foreverrafs.auth.AuthApi
import com.foreverrafs.superdiary.common.coroutines.TestAppDispatchers
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.auth.changepassword.ChangePasswordViewModel
import com.foreverrafs.superdiary.auth.changepassword.PasswordStrength
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class ChangePasswordViewModelTest {
    private lateinit var viewModel: ChangePasswordViewModel

    private val authApi: AuthApi = mock {
        everySuspend { updatePassword(any()) } returns Result.success(Unit)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(TestAppDispatchers.main)
        viewModel = ChangePasswordViewModel(
            logger = AggregateLogger(emptyList()),
            authApi = authApi,
            savedStateHandle = SavedStateHandle(),
        )
    }

    @Test
    fun `Should update state to strong password when a strong password is set`() = runTest {
        viewModel.onAction(
            ChangePasswordViewModel.ChangePasswordScreenAction.PasswordValueChange("P@sswOrd123"),
        )

        viewModel.viewState.test {
            val state = awaitItem()

            cancelAndIgnoreRemainingEvents()

            assertThat(state.passwordStrength).isNotNull()
            assertThat(state.passwordStrength).isEqualTo(PasswordStrength.Strong)
        }
    }

    @Test
    fun `Should update state to medium password when a strong password is set`() = runTest {
        viewModel.onAction(
            ChangePasswordViewModel.ChangePasswordScreenAction.PasswordValueChange("password@123"),
        )

        viewModel.viewState.test {
            val state = awaitItem()

            cancelAndIgnoreRemainingEvents()

            assertThat(state.passwordStrength).isNotNull()
            assertThat(state.passwordStrength).isEqualTo(PasswordStrength.Medium)
        }
    }

    @Test
    fun `Should update state to weak password when a strong password is set`() = runTest {
        viewModel.onAction(
            ChangePasswordViewModel.ChangePasswordScreenAction.PasswordValueChange("pass"),
        )

        viewModel.viewState.test {
            val state = awaitItem()

            cancelAndIgnoreRemainingEvents()

            assertThat(state.passwordStrength).isNotNull()
            assertThat(state.passwordStrength).isEqualTo(PasswordStrength.Weak)
        }
    }

    @Test
    fun `Should update state accordingly when passwords are not matching`() = runTest {
        viewModel.onAction(
            ChangePasswordViewModel.ChangePasswordScreenAction.PasswordValueChange("pass"),
        )
        viewModel.onAction(
            ChangePasswordViewModel.ChangePasswordScreenAction.ConfirmPasswordValueChange("pass1"),
        )

        viewModel.viewState.test {
            val state = awaitItem()

            cancelAndIgnoreRemainingEvents()

            assertThat(state.arePasswordsMatching).isNotNull()
            assertThat(state.arePasswordsMatching).isEqualTo(false)
        }
    }

    @Test
    fun `Should update state accordingly when passwords ARE not matching`() = runTest {
        viewModel.onAction(
            ChangePasswordViewModel.ChangePasswordScreenAction.PasswordValueChange("pass"),
        )
        viewModel.onAction(
            ChangePasswordViewModel.ChangePasswordScreenAction.ConfirmPasswordValueChange("pass"),
        )

        viewModel.viewState.test {
            val state = awaitItem()

            cancelAndIgnoreRemainingEvents()

            assertThat(state.arePasswordsMatching).isNotNull()
            assertThat(state.arePasswordsMatching).isEqualTo(true)
        }
    }

    @Test
    fun `Should update state to processing state when submit button is clicked`() = runTest {
        viewModel.onAction(
            ChangePasswordViewModel.ChangePasswordScreenAction.SubmitPasswordChange,
        )

        runCurrent()

        viewModel.viewState.test {
            val state = expectMostRecentItem()

            cancelAndIgnoreRemainingEvents()

            assertThat(state.isProcessing).isNotNull()
            assertThat(state.isProcessing).isEqualTo(true)
        }
    }

    @Test
    fun `Should submit password request when submit button is clicked`() = runTest {
        viewModel.onAction(
            ChangePasswordViewModel.ChangePasswordScreenAction.SubmitPasswordChange,
        )

        runCurrent()

        verifySuspend(VerifyMode.exactly(1)) {
            authApi.updatePassword(any())
        }
    }

    @Test
    fun `Should update to success state when password is succesfully changed`() = runTest {
        viewModel.onAction(
            ChangePasswordViewModel.ChangePasswordScreenAction.SubmitPasswordChange,
        )
        runCurrent()
        viewModel.viewState.test {
            val state = expectMostRecentItem()

            assertThat(state.isSuccess).isNotNull()
            assertThat(state.isSuccess).isEqualTo(true)
        }
    }

    @Test
    fun `Should update to failure state when password is succesfully changed`() = runTest {
        viewModel.onAction(
            ChangePasswordViewModel.ChangePasswordScreenAction.SubmitPasswordChange,
        )
        runCurrent()
        viewModel.viewState.test {
            val state = expectMostRecentItem()

            assertThat(state.isSuccess).isNotNull()
            assertThat(state.isSuccess).isEqualTo(true)
        }
    }

    @Test
    fun `Should skip password equality check if password is empty`() = runTest {
        viewModel.onAction(
            ChangePasswordViewModel.ChangePasswordScreenAction.PasswordValueChange(""),
        )

        runCurrent()
        viewModel.viewState.test {
            val state = expectMostRecentItem()

            assertThat(state.arePasswordsMatching).isNull()
        }
    }

    @Test
    fun `Should skip password equality check if password confirmation is empty`() = runTest {
        viewModel.onAction(
            ChangePasswordViewModel.ChangePasswordScreenAction.ConfirmPasswordValueChange(""),
        )

        runCurrent()
        viewModel.viewState.test {
            val state = expectMostRecentItem()

            assertThat(state.arePasswordsMatching).isNull()
        }
    }

    @Test
    fun `Should clear error messages when error messages are dismissed`() = runTest {
        viewModel.onAction(
            ChangePasswordViewModel.ChangePasswordScreenAction.DismissErrorMessage,
        )

        runCurrent()
        viewModel.viewState.test {
            val state = expectMostRecentItem()

            assertThat(state.isSuccess).isNull()
            assertThat(state.errorMessage).isNull()
        }
    }

    @Test
    fun `Should transition to error state when submit request fails`() = runTest {
        everySuspend { authApi.updatePassword(any()) } returns Result.failure(Exception("Error"))

        viewModel.onAction(
            ChangePasswordViewModel.ChangePasswordScreenAction.SubmitPasswordChange,
        )

        runCurrent()
        viewModel.viewState.test {
            val state = expectMostRecentItem()

            assertThat(state.errorMessage).isNotNull()
            assertThat(state.isSuccess).isEqualTo(false)
        }
    }
}
