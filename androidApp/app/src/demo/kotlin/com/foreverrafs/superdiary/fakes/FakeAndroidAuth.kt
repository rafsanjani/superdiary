package com.foreverrafs.superdiary.fakes

import com.foreverrafs.auth.AuthApi
import com.foreverrafs.auth.DefaultSupabaseAuth
import com.foreverrafs.auth.model.SessionInfo
import com.foreverrafs.auth.model.UserInfo
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import io.github.jan.supabase.SupabaseClient
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class FakeAndroidAuth(
    private val supabaseClient: SupabaseClient,
    private val logger: AggregateLogger,
) : AuthApi by DefaultSupabaseAuth(supabaseClient, logger) {

    private val userInfo = UserInfo(
        id = "random-user-id",
        avatarUrl = "avatar-url",
        name = "John Doe",
        email = "john.doe@gmail.com",
        uniqueEmail = "ooeikfkdiwiie@emailparse.nebulainnova.co.uk",
    )

    private val sessionInfo = SessionInfo(
        expiresAt = Instant.Companion.DISTANT_PAST,
        accessToken = "",
        refreshToken = "",
        userInfo = userInfo,
    )

    override suspend fun signIn(email: String, password: String): AuthApi.SessionStatus =
        AuthApi.SessionStatus.Authenticated(
            sessionInfo = sessionInfo,
        )

    override fun currentUserOrNull(): UserInfo? = userInfo

    override suspend fun signOut(): Result<Unit> = Result.success(Unit)

    // Always force a fresh authentication
    override suspend fun restoreSession(): AuthApi.SessionStatus =
        AuthApi.SessionStatus.Unauthenticated(Exception("User isn't logged in"))

    override suspend fun register(
        name: String,
        email: String,
        password: String,
    ): AuthApi.RegistrationStatus = AuthApi.RegistrationStatus.Success

    override suspend fun updatePassword(password: String): Result<Unit> = Result.success(Unit)
}
