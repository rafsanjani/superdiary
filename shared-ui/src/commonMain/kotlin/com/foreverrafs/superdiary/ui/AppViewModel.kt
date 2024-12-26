package com.foreverrafs.superdiary.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foreverrafs.auth.AuthApi
import com.foreverrafs.auth.model.SessionInfo
import com.foreverrafs.auth.model.UserInfo
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.core.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.ui.feature.auth.register.DeeplinkContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface AppSessionState {
    data object Processing : AppSessionState
    data class Success(val userInfo: UserInfo?) : AppSessionState
    data class Error(val exception: Exception) : AppSessionState
    data object UnAuthenticated : AppSessionState
}

class AppViewModel(
    private val appCoroutineDispatchers: AppCoroutineDispatchers,
    private val logger: AggregateLogger,
    private val authApi: AuthApi,
    private val deeplinkContainer: DeeplinkContainer,
) : ViewModel() {

    private val _viewState: MutableStateFlow<AppSessionState> =
        MutableStateFlow(AppSessionState.Processing)

    val viewState: StateFlow<AppSessionState> = _viewState.onStart {
        restoreSession()
    }
        .onEach {
            logger.d(TAG) {
                "Transitioning viewState to $it"
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AppSessionState.Processing,
        )

    /**
     * When there is a token available, we show a loading screen and
     * automatically sign in
     */
    private fun restoreSession() = viewModelScope.launch(appCoroutineDispatchers.main) {
        val session = processRegistrationDeeplink()

        if (session != null) {
            logger.d(TAG) {
                "Emitting success state from registration confirmation token"
            }
            _viewState.update {
                AppSessionState.Success(session.userInfo)
            }
            return@launch
        }
        logger.d(TAG) {
            "Unable to restore from registration confirmation token. Attempting to restore from regular session"
        }

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

    fun logOut() = viewModelScope.launch {
        authApi.signOut()
        _viewState.update { AppSessionState.UnAuthenticated }
    }

    private suspend fun processRegistrationDeeplink(): SessionInfo? {
        val deeplink = deeplinkContainer.getAndRemove(
            type = DeeplinkContainer.LinkType.EmailConfirmation,
        )
        if (deeplink == null || deeplink.payload.isEmpty()) {
            logger.d(TAG) { "No registration deeplink found" }
            return null
        }

        return when (
            val result =
                authApi.handleRegistrationConfirmationDeeplink(deeplink.payload)
        ) {
            is AuthApi.SignInStatus.LoggedIn -> {
                logger.d(TAG) {
                    "New account successfully confirmed. A session for the user should have been started by now"
                }
                result.sessionInfo
            }

            is AuthApi.SignInStatus.Error -> {
                logger.e(
                    tag = TAG,
                    throwable = result.exception,
                ) {
                    "Error confirming new user registration"
                }
                null
            }
        }
    }

    companion object {
        private const val TAG = "AppViewModel"
    }
}
