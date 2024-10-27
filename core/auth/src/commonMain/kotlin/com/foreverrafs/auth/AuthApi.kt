package com.foreverrafs.auth

import com.foreverrafs.superdiary.core.utils.ActivityWrapper

interface AuthApi {
    suspend fun signInWithGoogle(activityWrapper: ActivityWrapper): SignInStatus

    sealed interface SignInStatus {
        data object LoggedIn : SignInStatus
        data class Error(val exception: Exception) : SignInStatus
    }
}
