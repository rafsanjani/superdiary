package com.foreverrafs.auth

interface AuthApi {
    suspend fun signInWithGoogle(): SignInStatus

    sealed interface SignInStatus {
        data object LoggedIn : SignInStatus
        data class Error(val exception: Exception) : SignInStatus
    }
}
