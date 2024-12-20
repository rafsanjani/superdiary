package com.foreverrafs.auth

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
    ): SignInStatus

    suspend fun signOut()

    sealed interface SignInStatus {
        data class LoggedIn(val sessionInfo: SessionInfo) : SignInStatus
        data class Error(val exception: Exception) : SignInStatus
    }
}

class NoCredentialsException(message: String) : Exception(message)
