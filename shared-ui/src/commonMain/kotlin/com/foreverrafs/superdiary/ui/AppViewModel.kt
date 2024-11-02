package com.foreverrafs.superdiary.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foreverrafs.auth.AuthApi
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.core.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.data.utils.DiaryPreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface AppSessionState {
    data object Processing : AppSessionState
    data object Success : AppSessionState
    data class Error(val exception: Exception) : AppSessionState
}

class AppViewModel(
    private val diaryPreference: DiaryPreference,
    private val appCoroutineDispatchers: AppCoroutineDispatchers,
    private val logger: AggregateLogger,
    private val authApi: AuthApi,
) : ViewModel() {

    private val _viewState: MutableStateFlow<AppSessionState> =
        MutableStateFlow(AppSessionState.Processing)

    val viewState: StateFlow<AppSessionState> = _viewState.onStart {
        restoreSession()
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AppSessionState.Processing,
    )

    /**
     * When there is a token available, we show a loading screen and
     * automatically sign in
     */
    private fun restoreSession() = viewModelScope.launch(appCoroutineDispatchers.main) {
        val authorizationToken = getAuthorizationToken()

        if (authorizationToken.isEmpty()) {
            logger.d(TAG) { "No session token found. Navigating to sign-in screen." }
            _viewState.update {
                AppSessionState.Error(
                    Exception("No session token found"),
                )
            }
            return@launch
        }

        logger.d(TAG) { "Session token found. Attempting to sign in." }
        _viewState.update { AppSessionState.Processing }

        val authStatus = authApi.signInWithGoogle(googleIdToken = authorizationToken)

        _viewState.update {
            when (authStatus) {
                is AuthApi.SignInStatus.Error -> {
                    logger.e(TAG) { "Sign-in failed: ${authStatus.exception}" }
                    AppSessionState.Error(authStatus.exception)
                }

                is AuthApi.SignInStatus.LoggedIn -> {
                    logger.d(TAG) { "Sign-in successful. Navigating to home screen." }
                    AppSessionState.Success
                }
            }
        }
    }

    private suspend fun getAuthorizationToken(): String {
        val preferences = withContext(appCoroutineDispatchers.io) {
            diaryPreference.snapshot
        }

        return preferences.authorizationToken
    }

    companion object {
        private const val TAG = "AppViewModel"
    }
}
