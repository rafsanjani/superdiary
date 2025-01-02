package com.foreverrafs.superdiary.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foreverrafs.auth.AuthApi
import com.foreverrafs.auth.model.SessionInfo
import com.foreverrafs.auth.model.UserInfo
import com.foreverrafs.superdiary.common.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.Result
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
    data class Authenticated(
        val userInfo: UserInfo?,
        val linkType: DeeplinkContainer.LinkType? = null,
    ) : AppSessionState

    data class Error(
        val exception: Throwable,
        val isFromDeeplink: Boolean = false,
    ) : AppSessionState

    data object UnAuthenticated : AppSessionState
}

class AppViewModel(
    private val appCoroutineDispatchers: AppCoroutineDispatchers,
    private val logger: AggregateLogger,
    private val authApi: AuthApi,
    deeplinkContainer: DeeplinkContainer,
) : ViewModel() {

    private val _viewState: MutableStateFlow<AppSessionState> =
        MutableStateFlow(AppSessionState.Processing)

    private val pendingDeeplink = deeplinkContainer.pop()
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
     * Attempt to restore a valid session from a deeplink when available When
     * the deeplink is invalid a special error is emitted and is used to render
     * the ui accordingly
     */
    private fun restoreSession() = viewModelScope.launch(appCoroutineDispatchers.main) {
        if (pendingDeeplink != null) {
            if (pendingDeeplink.type == DeeplinkContainer.LinkType.Invalid) {
                logger.d(TAG) {
                    "Invalid deeplink found. Emitting error state $pendingDeeplink"
                }
                _viewState.update {
                    AppSessionState.Error(
                        isFromDeeplink = true,
                        exception = Exception(pendingDeeplink.payload.toString()),
                    )
                }
                return@launch
            }

            logger.d(TAG) {
                "App deeplink found. attempting to process it"
            }
            when (val getSessionResult = processPendingDeeplink()) {
                is Result.Success -> {
                    "Emitting success state from authentication token"
                    _viewState.update {
                        AppSessionState.Authenticated(
                            userInfo = getSessionResult.data.userInfo,
                            linkType = pendingDeeplink.type,
                        )
                    }
                    return@launch
                }

                is Result.Failure -> {
                    _viewState.update {
                        AppSessionState.Error(
                            isFromDeeplink = true,
                            exception = getSessionResult.error,
                        )
                    }
                }
            }
            return@launch
        }

        logger.d(TAG) {
            "Unable to restore from registration confirmation token. Attempting to restore from regular session"
        }

        val sessionRestoreStatus = authApi.restoreSession()

        _viewState.update {
            when (sessionRestoreStatus) {
                is AuthApi.SignInStatus.Error -> {
                    logger.w(
                        tag = TAG,
                        throwable = sessionRestoreStatus.exception,
                    ) { "Unable to restore previous session" }
                    AppSessionState.Error(
                        exception = sessionRestoreStatus.exception,
                        isFromDeeplink = false,
                    )
                }

                is AuthApi.SignInStatus.LoggedIn -> {
                    logger.d(TAG) { "Session restored. Token expires on ${sessionRestoreStatus.sessionInfo.expiresAt}" }
                    logger.d(TAG) { "Session user ${sessionRestoreStatus.sessionInfo.userInfo}" }
                    AppSessionState.Authenticated(sessionRestoreStatus.sessionInfo.userInfo)
                }
            }
        }
    }

    fun logOut() = viewModelScope.launch {
        authApi.signOut()
        _viewState.update { AppSessionState.UnAuthenticated }
    }

    /**
     * An auth payload can originate from any of the following sources;
     * 1. confirmation email sent after a registration process in the app
     * 2. An invitation email sent after a user is invited
     * 3. A password reset email after a user tries to reset their password
     */
    private suspend fun processPendingDeeplink(): Result<SessionInfo> = when (
        val result =
            authApi.handleAuthDeeplink(pendingDeeplink?.payload)
    ) {
        is AuthApi.SignInStatus.LoggedIn -> {
            logger.d(TAG) {
                "Auth deeplink successfully processed. A session for the user should have been started by now"
            }
            Result.Success(result.sessionInfo)
        }

        is AuthApi.SignInStatus.Error -> {
            logger.e(
                tag = TAG,
                throwable = result.exception,
            ) {
                "Error confirming new user registration"
            }
            Result.Failure(Exception("Error authenticating user with link payload"))
        }
    }

    companion object {
        private const val TAG = "AppViewModel"
    }
}
