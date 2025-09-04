package com.foreverrafs.auth

import androidx.core.uri.Uri
import com.foreverrafs.auth.AuthApi.SessionStatus.Authenticated
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
    override suspend fun signInWithGoogle(): AuthApi.SessionStatus =
        try {
            client.auth.signInWith(provider = Google)
            getSessionStatus()
        } catch (e: RestException) {
            AuthApi.SessionStatus.Unauthenticated(e)
        }

    override suspend fun signInWithGoogle(googleIdToken: String): AuthApi.SessionStatus = try {
        client.auth.signInWith(IDToken) {
            idToken = googleIdToken
            provider = Google
        }
        getSessionStatus()
    } catch (e: RestException) {
        logger.e(tag = Tag, throwable = e)
        if (e is BadRequestRestException) {
            // Rewrite exception into a domain type
            AuthApi.SessionStatus.Unauthenticated(TokenExpiredException(message = e.message))
        } else {
            AuthApi.SessionStatus.Unauthenticated(e)
        }
    }

    override fun sessionStatus(): Flow<AuthApi.SessionStatus> =
        client.auth.sessionStatus.map { sessionStatus ->
            when (sessionStatus) {
                is SessionStatus.Authenticated -> Authenticated(
                    sessionInfo = sessionStatus.session.toSession(),
                )

                is SessionStatus.Initializing -> AuthApi.SessionStatus.Unauthenticated(
                    Exception("Session is initializing"),
                )

                is SessionStatus.NotAuthenticated -> AuthApi.SessionStatus.Unauthenticated(
                    Exception("User is not authenticated"),
                )

                is SessionStatus.RefreshFailure -> AuthApi.SessionStatus.Unauthenticated(
                    Exception("Session refresh failed"),
                )
            }
        }

    private suspend fun getSessionStatus(): AuthApi.SessionStatus {
        val currentSession = client.auth.currentSessionOrNull()

        return if (currentSession != null) {
            associateUniqueEmailToUser()
            AuthApi.SessionStatus.Authenticated(currentSession.toSession())
        } else {
            logger.e(Tag) {
                "User was authenticated, but the session was null."
            }
            AuthApi.SessionStatus.Unauthenticated(Exception("User was authenticated, but the session was null."))
        }
    }

    override suspend fun restoreSession(): AuthApi.SessionStatus {
        // Wait for session to be initialized
        while (client.auth.sessionStatus.value == SessionStatus.Initializing) {
            logger.i(Tag) {
                "Waiting for session to be initialized"
            }
            delay(100)
        }

        logger.i(Tag) {
            "Session Initialized. Attempting to get current session"
        }
        val currentSession = client.auth.currentSessionOrNull()

        return if (currentSession != null) {
            AuthApi.SessionStatus.Authenticated(
                currentSession.toSession(),
            )
        } else {
            AuthApi.SessionStatus.Unauthenticated(Exception("No session information found!"))
        }
    }

    override suspend fun signIn(email: String, password: String): AuthApi.SessionStatus = try {
        client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        getSessionStatus()
    } catch (exception: Exception) {
        AuthApi.SessionStatus.Unauthenticated(exception.transform())
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
        AuthApi.RegistrationStatus.Error(
            exception.transform(),
        )
    }

    private fun Exception.transform(): AuthException = when {
        message?.contains(
            USER_REGISTERED_ERROR,
            true,
        ) == true -> UserAlreadyRegisteredException(message.orEmpty())

        message?.contains(
            INVALID_LOGIN_CREDENTIALS,
            true,
        ) == true -> InvalidCredentialsException(message.orEmpty())

        else -> {
            GenericAuthException(this, this.message)
        }
    }

    private suspend fun associateUniqueEmailToUser() {
        val currentUniqueEmail = currentUserOrNull()?.uniqueEmail

        if (currentUniqueEmail.isNullOrBlank()) {
            logger.i(Tag) {
                "User already has a unique email. Skipping update. Email is $currentUniqueEmail"
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
        logger.i(Tag) {
            "Associated unique email with user."
        }
    }

    override suspend fun updatePassword(password: String): Result<Unit> = try {
        logger.i(Tag) {
            "Setting new password for user!"
        }

        client.auth.updateUser {
            this.password = password
        }
        Result.success(Unit)
    } catch (e: Exception) {
        logger.e(Tag, e) {
            "Error setting new password for user!"
        }
        Result.failure(e)
    }

    override suspend fun signOut(): Result<Unit> = try {
        client.auth.signOut()
        Result.success(Unit)
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        logger.e(Tag, e)
        Result.failure(e)
    }

    override fun currentUserOrNull(): UserInfo? =
        client.auth.currentUserOrNull()?.toUserInfo()

    @OptIn(SupabaseInternal::class)
    override suspend fun handleAuthDeeplink(deeplinkUri: Uri?): AuthApi.SessionStatus =
        suspendCoroutine { continuation ->
            logger.i(Tag) {
                "Confirming authentication with token $deeplinkUri"
            }

            if (deeplinkUri.toString().contains("error")) {
                continuation.resume(
                    AuthApi.SessionStatus.Unauthenticated(Exception("Invalid confirmation link")),
                )
                return@suspendCoroutine
            }

            try {
                client.auth.parseFragmentAndImportSession(
                    fragment = deeplinkUri?.getFragment().orEmpty(),
                    onFinish = { session ->
                        continuation.resume(
                            if (session != null) {
                                AuthApi.SessionStatus.Authenticated(session.toSession())
                            } else {
                                AuthApi.SessionStatus.Unauthenticated(Exception("Error logging in"))
                            },
                        )
                    },
                )
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                continuation.resume(
                    AuthApi.SessionStatus.Unauthenticated(e),
                )
            }
        }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> = try {
        logger.i(Tag) {
            "Sending password reset email to $email"
        }
        client.auth.resetPasswordForEmail(
            email,
        )
        logger.i(Tag) {
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
        private const val INVALID_LOGIN_CREDENTIALS = "Invalid login credentials"
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
    uniqueEmail = userMetadata?.get("unique_email").toString().trim('\"'),
)
