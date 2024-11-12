package com.foreverrafs.superdiary.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foreverrafs.auth.AuthApi
import com.foreverrafs.auth.model.UserInfo
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.core.utils.AppCoroutineDispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface AppSessionState {
    data object Processing : AppSessionState
    data class Success(val userInfo: UserInfo?) : AppSessionState
    data class Error(val exception: Exception) : AppSessionState
}

class AppViewModel(
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
        val authStatus = authApi.restoreSession()

        _viewState.update {
            when (authStatus) {
                is AuthApi.SignInStatus.Error -> {
                    logger.w(
                        tag = TAG,
                        throwable = authStatus.exception,
                    ) { "Unable to restore previous session" }
                    AppSessionState.Error(authStatus.exception)
                }

                is AuthApi.SignInStatus.LoggedIn -> {
                    logger.d(TAG) { "Session restored. Token expires on ${authStatus.sessionInfo.expiresAt}" }
                    logger.d(TAG) { "Session user ${authStatus.sessionInfo.userInfo}" }
                    AppSessionState.Success(authStatus.sessionInfo.userInfo)
                }
            }
        }
    }

    companion object {
        private const val TAG = "AppViewModel"
    }
}
