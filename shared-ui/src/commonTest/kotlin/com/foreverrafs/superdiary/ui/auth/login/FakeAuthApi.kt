package com.foreverrafs.superdiary.ui.auth.login

import com.foreverrafs.auth.AuthApi
import com.foreverrafs.auth.model.SessionInfo
import com.foreverrafs.auth.model.UserInfo
import com.foreverrafs.superdiary.core.utils.ActivityWrapper
import kotlinx.datetime.Clock

class FakeAuthApi(
    clock: Clock = Clock.System,
) : AuthApi {
    var signInResult: AuthApi.SignInStatus = AuthApi.SignInStatus.LoggedIn(
        SessionInfo(
            expiresAt = clock.now(),
            accessToken = "test-access-token",
            refreshToken = "test-refresh-token",
            userInfo = UserInfo(
                id = "user-id",
                name = "use-rname",
                email = "john@doe.com",
                avatarUrl = "avatar-url",
            ),
        ),
    )

    override suspend fun signInWithGoogle(activityWrapper: ActivityWrapper?): AuthApi.SignInStatus =
        signInResult

    override suspend fun signInWithGoogle(googleIdToken: String): AuthApi.SignInStatus =
        signInResult

    override suspend fun restoreSession(): AuthApi.SignInStatus = signInResult
}
