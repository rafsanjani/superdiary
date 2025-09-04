package com.foreverrafs.auth

import androidx.core.uri.Uri
import com.foreverrafs.auth.model.SessionInfo
import com.foreverrafs.auth.model.UserInfo
import kotlinx.coroutines.flow.Flow

class TokenExpiredException(message: String?) : Exception(message)

interface AuthApi {
    suspend fun signInWithGoogle(): SessionStatus
    suspend fun signInWithGoogle(googleIdToken: String): SessionStatus

    suspend fun restoreSession(): SessionStatus

    fun sessionStatus(): Flow<SessionStatus>
    suspend fun signIn(email: String, password: String): SessionStatus
    suspend fun register(
        name: String,
        email: String,
        password: String,
    ): RegistrationStatus

    suspend fun signOut(): Result<Unit>

    suspend fun handleAuthDeeplink(deeplinkUri: Uri?): SessionStatus

    suspend fun sendPasswordResetEmail(email: String): Result<Unit>

    suspend fun updatePassword(password: String): Result<Unit>

    fun currentUserOrNull(): UserInfo?

    sealed interface SessionStatus {
        data class Authenticated(val sessionInfo: SessionInfo) : SessionStatus
        data class Unauthenticated(val exception: Exception) : SessionStatus
    }

    sealed interface RegistrationStatus {
        // After registration, the user has to verify their email so there is no session info
        data object Success : RegistrationStatus
        data class Error(val exception: Exception) : RegistrationStatus
    }
}

open class AuthException(message: String? = null, cause: Throwable? = null) :
    Exception(message, cause)

// When the user hasn't enrolled any of the requested credentials onto their device yet
class NoCredentialsException(message: String) : AuthException(message)

// Wrong username and password
class InvalidCredentialsException(message: String) : AuthException(message)

// User has already been registered::duplicate registration
class UserAlreadyRegisteredException(message: String) : AuthException(message)

// Everything else
class GenericAuthException(cause: Throwable, message: String?) :
    AuthException(cause = cause, message = message)
