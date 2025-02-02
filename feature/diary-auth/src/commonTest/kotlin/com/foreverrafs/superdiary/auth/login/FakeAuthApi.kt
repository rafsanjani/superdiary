package com.foreverrafs.superdiary.auth.login

import androidx.core.uri.Uri
import com.foreverrafs.auth.AuthApi
import com.foreverrafs.auth.model.SessionInfo
import com.foreverrafs.auth.model.UserInfo
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

    var sendPasswordResetEmailResult: Result<Unit> = Result.success(Unit)
    var registerWithEmailResult: AuthApi.RegistrationStatus = AuthApi.RegistrationStatus.Success
    var signOutResult = Result.success(Unit)

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> =
        sendPasswordResetEmailResult

    override suspend fun handleAuthDeeplink(deeplinkUri: Uri?): AuthApi.SignInStatus {
        TODO("Not yet implemented")
    }

    override suspend fun signInWithGoogle(): AuthApi.SignInStatus =
        signInResult

    override suspend fun signInWithGoogle(googleIdToken: String): AuthApi.SignInStatus =
        signInResult

    override suspend fun restoreSession(): AuthApi.SignInStatus = signInResult

    override suspend fun signIn(email: String, password: String): AuthApi.SignInStatus =
        signInResult

    override suspend fun currentUserOrNull(): UserInfo? =
        (signInResult as? AuthApi.SignInStatus.LoggedIn)?.sessionInfo?.userInfo

    override suspend fun register(
        name: String,
        email: String,
        password: String,
    ): AuthApi.RegistrationStatus = registerWithEmailResult

    override suspend fun signOut() = signOutResult
}
