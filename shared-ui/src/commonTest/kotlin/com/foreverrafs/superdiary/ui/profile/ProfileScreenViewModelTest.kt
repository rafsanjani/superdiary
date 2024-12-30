package com.foreverrafs.superdiary.ui.profile

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isNotEmpty
import com.foreverrafs.auth.AuthApi
import com.foreverrafs.superdiary.TestAppDispatchers
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

    private val authApi: AuthApi = FakeAuthApi()

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
}
