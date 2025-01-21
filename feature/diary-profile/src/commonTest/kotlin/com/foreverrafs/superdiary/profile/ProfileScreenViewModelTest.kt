package com.foreverrafs.superdiary.profile

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.foreverrafs.auth.AuthApi
import com.foreverrafs.auth.model.UserInfo
import com.foreverrafs.superdiary.profile.presentation.ProfileScreenViewModel
import com.foreverrafs.superdiary.utils.DiaryPreference
import com.foreverrafs.superdiary.utils.DiarySettings
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

class ProfileScreenViewModelTest {
    private lateinit var profileScreenViewModel: ProfileScreenViewModel

    // TODO: Replace this mock with a fake
    private val authApi: AuthApi = mock()
    private val preference: DiaryPreference = mock()

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())

        every { preference.settings } returns flowOf(DiarySettings.Empty)
        profileScreenViewModel = ProfileScreenViewModel(
            authApi = authApi,
            preference = preference,
        )
    }

    @Test
    fun `Should load user data when profile screen loads`() = runTest {
        everySuspend { authApi.currentUserOrNull() }.returns(
            UserInfo(
                id = "id",
                avatarUrl = "avatarUrl",
                name = "Rafsanjani Aziz",
                email = "coded_raf@yahoo.com",
            ),
        )
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
        everySuspend { authApi.signOut() }.returns(Result.failure(Exception("Error signing out")))
        everySuspend { authApi.currentUserOrNull() }.returns(null)

        profileScreenViewModel.viewState.test {
            skipItems(1)
            profileScreenViewModel.onLogout()

            val state = awaitItem()

            // error messages should all be set by now
            assertThat(state.errorMessage).isNotNull()

            profileScreenViewModel.resetErrors()
            val currentState = awaitItem()
            assertThat(currentState.errorMessage).isNull()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Should update settings with new values when settings is changed`() = runTest {
        everySuspend { authApi.currentUserOrNull() } returns null
        everySuspend { preference.save(any()) } returns Unit

        val settings = profileScreenViewModel.settings.value
        val updatedSettings = settings.copy(isFirstLaunch = true)

        profileScreenViewModel.onSettingsUpdated(
            updatedSettings,
        )

        advanceUntilIdle()

        verifySuspend {
            preference.save(any())
        }
    }
}
