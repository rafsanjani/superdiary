package com.foreverrafs.auth

import androidx.core.uri.Uri
import com.foreverrafs.auth.model.SessionInfo
import com.foreverrafs.superdiary.core.utils.ActivityWrapper

class TokenExpiredException(message: String?) : Exception(message)

interface AuthApi {
    suspend fun signInWithGoogle(activityWrapper: ActivityWrapper?): SignInStatus
    suspend fun signInWithGoogle(googleIdToken: String): SignInStatus

    suspend fun restoreSession(): SignInStatus
    suspend fun signIn(email: String, password: String): SignInStatus
    suspend fun register(
        name: String,
        email: String,
        password: String,
    ): RegistrationStatus

    suspend fun signOut()

    suspend fun handleAuthDeeplink(deeplinkUri: Uri?): SignInStatus

    suspend fun sendPasswordResetEmail(email: String): Result<Unit>

    sealed interface SignInStatus {
        data class LoggedIn(val sessionInfo: SessionInfo) : SignInStatus
        data class Error(val exception: Exception) : SignInStatus
    }

    sealed interface RegistrationStatus {
        // After registration, the user has to verify their email so there is no session info
        data object Success : RegistrationStatus
        data class Error(val exception: Exception) : RegistrationStatus
    }
}

class NoCredentialsException(message: String) : Exception(message)
class UserAlreadyRegisteredException(message: String) : Exception(message)
