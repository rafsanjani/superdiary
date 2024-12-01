package com.foreverrafs.auth

import com.foreverrafs.auth.model.SessionInfo
import com.foreverrafs.auth.model.UserInfo
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.core.utils.ActivityWrapper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.exceptions.BadRequestRestException
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.delay

typealias UserInfoDto = io.github.jan.supabase.auth.user.UserInfo
typealias SessionInfoDto = UserSession

/**
 * Provides default implementation for all the functions in [AuthApi]
 * to adhere to interface segregation. Platform classes will implement
 * [AuthApi] by using this class as a delegate and overriding some of the
 * functions
 */
class DefaultSupabaseAuth(
    private val client: SupabaseClient,
    private val logger: AggregateLogger,
) : AuthApi {
    override suspend fun signInWithGoogle(activityWrapper: ActivityWrapper?): AuthApi.SignInStatus =
        try {
            client.auth.signInWith(provider = Google)
            getSessionStatus()
        } catch (e: RestException) {
            AuthApi.SignInStatus.Error(e)
        }

    override suspend fun signInWithGoogle(googleIdToken: String): AuthApi.SignInStatus = try {
        client.auth.signInWith(IDToken) {
            idToken = googleIdToken
            provider = Google
        }
        getSessionStatus()
    } catch (e: RestException) {
        if (e is BadRequestRestException) {
            // Rewrite exception into a domain type
            AuthApi.SignInStatus.Error(TokenExpiredException(message = e.message))
        } else {
            AuthApi.SignInStatus.Error(e)
        }
    }

    private fun getSessionStatus(): AuthApi.SignInStatus {
        val currentSession = client.auth.currentSessionOrNull()

        return if (currentSession != null) {
            AuthApi.SignInStatus.LoggedIn(currentSession.toSession())
        } else {
            AuthApi.SignInStatus.Error(Exception("User was authenticated, but the session was null."))
        }
    }

    override suspend fun restoreSession(): AuthApi.SignInStatus {
        // Wait for session to be initialized
        while (client.auth.sessionStatus.value == SessionStatus.Initializing) {
            logger.d(Tag) {
                "Waiting for session to be initialized"
            }
            delay(100)
        }

        logger.d(Tag) {
            "Session Initialized. Attempting to get current session"
        }
        val currentSession = client.auth.currentSessionOrNull()

        return if (currentSession != null) {
            AuthApi.SignInStatus.LoggedIn(
                currentSession.toSession(),
            )
        } else {
            AuthApi.SignInStatus.Error(Exception("No session information found!"))
        }
    }

    companion object {
        private val Tag = DefaultSupabaseAuth::class.simpleName.orEmpty()
    }

    override suspend fun signIn(email: String, password: String): AuthApi.SignInStatus = try {
        client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }

        val session = client.auth.currentSessionOrNull()
        if (session != null) {
            AuthApi.SignInStatus.LoggedIn(session.toSession())
        } else {
            AuthApi.SignInStatus.Error(Exception("User was signed in but the returning session is null!"))
        }
    } catch (e: Exception) {
        AuthApi.SignInStatus.Error(e)
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String,
    ): AuthApi.SignInStatus = try {
        client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }

        val session = client.auth.currentSessionOrNull()

        if (session != null) {
            AuthApi.SignInStatus.LoggedIn(session.toSession())
        } else {
            AuthApi.SignInStatus.Error(Exception("User was registered but returned session is null!"))
        }
    } catch (e: Exception) {
        AuthApi.SignInStatus.Error(e)
    }

    override suspend fun signOut() {
        client.auth.signOut()
    }
}

internal fun SessionInfoDto.toSession() = SessionInfo(
    expiresAt = expiresAt,
    accessToken = accessToken,
    refreshToken = refreshToken,
    userInfo = user?.toUserInfo(),
)

internal fun UserInfoDto.toUserInfo(): UserInfo = UserInfo(
    id = id,
    name = userMetadata?.get("name").toString(),
    email = userMetadata?.get("email").toString(),
    // Strip leading and ending quotes from avatar url
    avatarUrl = userMetadata?.get("avatar_url").toString().replace("^\"|\"$".toRegex(), ""),
)
