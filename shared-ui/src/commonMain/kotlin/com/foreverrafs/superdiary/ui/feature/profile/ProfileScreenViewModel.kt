package com.foreverrafs.superdiary.ui.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foreverrafs.auth.AuthApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileScreenViewData(
    val name: String = "",
    val email: String = "",
    val avatarUrl: String = "",
)

class ProfileScreenViewModel(
    private val authApi: AuthApi,
) : ViewModel() {
    private val _viewState: MutableStateFlow<ProfileScreenViewData> = MutableStateFlow(
        ProfileScreenViewData(),
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

        _viewState.value = _viewState.value.copy(
            name = profile?.name.orEmpty(),
            email = profile?.email.orEmpty(),
            avatarUrl = profile?.avatarUrl.orEmpty(),
        )
    }

    fun onSignOut() = viewModelScope.launch {
        authApi.signOut()
    }
}
