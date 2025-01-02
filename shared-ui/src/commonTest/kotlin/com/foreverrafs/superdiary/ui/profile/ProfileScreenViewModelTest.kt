package com.foreverrafs.superdiary.ui.profile

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.foreverrafs.superdiary.common.coroutines.TestAppDispatchers
import com.foreverrafs.superdiary.ui.auth.login.FakeAuthApi
import com.foreverrafs.superdiary.ui.feature.profile.ProfileScreenViewModel
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

class ProfileScreenViewModelTest {
    private lateinit var profileScreenViewModel: ProfileScreenViewModel

    private val authApi = FakeAuthApi()

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(TestAppDispatchers.main)
        profileScreenViewModel = ProfileScreenViewModel(authApi)
    }

    @Test
    fun `Should load user data when profile screen loads`() = runTest {
        profileScreenViewModel.viewState.test {
            // skip the initial state
            skipItems(1)

            val state = awaitItem()

            assertThat(state.name).isNotEmpty()
            assertThat(state.email).isNotEmpty()
            assertThat(state.avatarUrl).isNotEmpty()
        }
    }

    @Test
    fun `Should reset all error messages`() = runTest {
        authApi.signOutResult = Result.failure(Exception("Error signing out"))
        profileScreenViewModel.viewState.test {
            skipItems(2)
            profileScreenViewModel.onLogout()

            val state = awaitItem()

            // error messages should all be set by now
            assertThat(state.errorMessage).isNotNull()

            profileScreenViewModel.resetErrors()
            val currentState = awaitItem()
            assertThat(currentState.errorMessage).isNull()
        }
    }
}
