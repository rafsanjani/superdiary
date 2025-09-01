package com.foreverrafs.superdiary.auth.login

import androidx.core.uri.Uri
import com.foreverrafs.auth.AuthApi
import com.foreverrafs.auth.model.SessionInfo
import com.foreverrafs.auth.model.UserInfo
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class FakeAuthApi
constructor(
    clock: Clock = Clock.System,
) : AuthApi {
    var signInResult: AuthApi.SessionStatus = AuthApi.SessionStatus.Authenticated(
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

    override suspend fun handleAuthDeeplink(deeplinkUri: Uri?): AuthApi.SessionStatus {
        TODO("Not yet implemented")
    }

    override suspend fun updatePassword(password: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun signInWithGoogle(): AuthApi.SessionStatus =
        signInResult

    override suspend fun signInWithGoogle(googleIdToken: String): AuthApi.SessionStatus =
        signInResult

    override suspend fun restoreSession(): AuthApi.SessionStatus = signInResult

    override suspend fun signIn(email: String, password: String): AuthApi.SessionStatus =
        signInResult

    override fun currentUserOrNull(): UserInfo? =
        (signInResult as? AuthApi.SessionStatus.Authenticated)?.sessionInfo?.userInfo

    override suspend fun register(
        name: String,
        email: String,
        password: String,
    ): AuthApi.RegistrationStatus = registerWithEmailResult

    override suspend fun signOut() = signOutResult
}
