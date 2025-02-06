package com.foreverrafs.auth

import androidx.core.uri.Uri
import com.foreverrafs.auth.model.SessionInfo
import com.foreverrafs.auth.model.UserInfo
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.parseFragmentAndImportSession
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.exceptions.BadRequestRestException
import io.github.jan.supabase.exceptions.RestException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

internal typealias UserInfoDto = io.github.jan.supabase.auth.user.UserInfo
internal typealias SessionInfoDto = UserSession

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
    override suspend fun signInWithGoogle(): AuthApi.SignInStatus =
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
        logger.e(tag = Tag, throwable = e)
        if (e is BadRequestRestException) {
            // Rewrite exception into a domain type
            AuthApi.SignInStatus.Error(TokenExpiredException(message = e.message))
        } else {
            AuthApi.SignInStatus.Error(e)
        }
    }

    private suspend fun getSessionStatus(): AuthApi.SignInStatus {
        val currentSession = client.auth.currentSessionOrNull()

        return if (currentSession != null) {
            associateUniqueEmailToUser()
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

    override suspend fun signIn(email: String, password: String): AuthApi.SignInStatus = try {
        client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        getSessionStatus()
    } catch (e: Exception) {
        AuthApi.SignInStatus.Error(e)
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String,
    ): AuthApi.RegistrationStatus = try {
        client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
            data = buildJsonObject {
                put("full_name", JsonPrimitive(name))
            }
        }

        AuthApi.RegistrationStatus.Success
    } catch (exception: RestException) {
        val error = if (
            exception.message?.contains(USER_REGISTERED_ERROR, true) == true
        ) {
            UserAlreadyRegisteredException(exception.message.orEmpty())
        } else {
            exception
        }

        AuthApi.RegistrationStatus.Error(error)
    }

    private suspend fun associateUniqueEmailToUser() {
        if (!currentUserOrNull()?.uniqueEmail.isNullOrEmpty()) {
            logger.d(Tag) {
                "User already has a unique email. Skipping update."
            }
            return
        }

        val charset = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        val randomPart = (1..16)
            .map { charset.random() }
            .joinToString("")
        val uniqueEmail = "$randomPart@emailparse.nebulainnova.co.uk"

        client.auth.updateUser {
            data = buildJsonObject {
                put("unique_email", JsonPrimitive(uniqueEmail))
            }
        }
    }

    override suspend fun signOut(): Result<Unit> = try {
        client.auth.signOut()
        Result.success(Unit)
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        logger.e(Tag, e)
        Result.failure(e)
    }

    override suspend fun currentUserOrNull(): UserInfo? =
        client.auth.currentUserOrNull()?.toUserInfo()

    @OptIn(SupabaseInternal::class)
    override suspend fun handleAuthDeeplink(deeplinkUri: Uri?): AuthApi.SignInStatus =
        suspendCoroutine { continuation ->
            logger.d(Tag) {
                "Confirming authentication with token $deeplinkUri"
            }

            if (deeplinkUri.toString().contains("error")) {
                continuation.resume(
                    AuthApi.SignInStatus.Error(Exception("Invalid confirmation link")),
                )
                return@suspendCoroutine
            }

            try {
                client.auth.parseFragmentAndImportSession(
                    fragment = deeplinkUri?.getFragment().orEmpty(),
                    onSessionSuccess = {
                        continuation.resume(
                            AuthApi.SignInStatus.LoggedIn(it.toSession()),
                        )
                    },
                )
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                continuation.resume(
                    AuthApi.SignInStatus.Error(e),
                )
            }
        }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> = try {
        logger.d(Tag) {
            "Sending password reset email to $email"
        }
        client.auth.resetPasswordForEmail(
            email,
        )
        logger.d(Tag) {
            "Password reset email sent to $email"
        }
        Result.success(Unit)
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        logger.e(Tag) {
            "Failed to send password reset email to $email"
        }
        Result.failure(e)
    }

    companion object {
        private const val USER_REGISTERED_ERROR = "User already registered"
        private val Tag = DefaultSupabaseAuth::class.simpleName.orEmpty()
    }
}

internal fun SessionInfoDto.toSession() = SessionInfo(
    expiresAt = expiresAt,
    accessToken = accessToken,
    refreshToken = refreshToken,
    userInfo = user?.toUserInfo(),
)

// Strip leading and ending quotes from all the properties
internal fun UserInfoDto.toUserInfo(): UserInfo = UserInfo(
    id = id,
    name = userMetadata?.get("full_name").toString().trim('\"'),
    email = userMetadata?.get("email").toString().trim('\"'),
    avatarUrl = userMetadata?.get("avatar_url").toString().trim('\"'),
)
