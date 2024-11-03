package com.foreverrafs.auth

import com.foreverrafs.superdiary.core.utils.ActivityWrapper

class TokenExpiredException(message: String?) : Exception(message)

interface AuthApi {
    suspend fun signInWithGoogle(activityWrapper: ActivityWrapper): SignInStatus
    suspend fun signInWithGoogle(googleIdToken: String): SignInStatus

    sealed interface SignInStatus {
        data class LoggedIn(val token: String? = null) : SignInStatus
        data class Error(val exception: Exception) : SignInStatus
    }
}
