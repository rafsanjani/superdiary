package com.foreverrafs.superdiary.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foreverrafs.auth.AuthApi
import com.foreverrafs.superdiary.utils.DiaryPreference
import com.foreverrafs.superdiary.utils.DiarySettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileScreenViewData(
    val name: String = "",
    val email: String = "",
    val avatarUrl: String = "",
    val errorMessage: String? = null,
    val isLogoutSuccess: Boolean? = null,
)

class ProfileScreenViewModel(
    private val authApi: AuthApi,
    private val preference: DiaryPreference,
) : ViewModel() {
    private val _viewState: MutableStateFlow<ProfileScreenViewData> = MutableStateFlow(
        ProfileScreenViewData(),
    )

    val settings = preference.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DiarySettings.Empty,
    )

    val viewState = _viewState.asStateFlow()
        .onStart { loadProfileData() }
        .stateIn(
            scope = viewModelScope,
            initialValue = ProfileScreenViewData(),
            started = SharingStarted.WhileSubscribed(5000),
        )

    private fun loadProfileData() = viewModelScope.launch {
        val profile = authApi.currentUserOrNull()

        _viewState.update {
            it.copy(
                name = profile?.name.orEmpty(),
                email = profile?.email.orEmpty(),
                avatarUrl = profile?.avatarUrl.orEmpty(),
            )
        }
    }

    fun onLogout() = viewModelScope.launch {
        val result = authApi.signOut()

        _viewState.update {
            if (result.isSuccess) {
                preference.clear()
                it.copy(isLogoutSuccess = true)
            } else {
                it.copy(
                    errorMessage = "Error signing out",
                )
            }
        }
    }

    fun onSettingsUpdated(settings: DiarySettings) = viewModelScope.launch {
        preference.save {
            settings
        }
    }

    fun resetErrors() {
        _viewState.update {
            it.copy(
                errorMessage = null,
                isLogoutSuccess = null,
            )
        }
    }
}
