package com.foreverrafs.auth

import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialInterruptedException
import androidx.credentials.exceptions.GetCredentialProviderConfigurationException
import androidx.credentials.exceptions.GetCredentialUnknownException
import androidx.credentials.exceptions.NoCredentialException
import com.foreverrafs.superdiary.core.SuperDiarySecret
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import io.github.jan.supabase.SupabaseClient

class AndroidAuth(
    private val supabaseClient: SupabaseClient,
    private val logger: AggregateLogger,
) : AuthApi by DefaultSupabaseAuth(
    client = supabaseClient,
    logger = logger,
) {
    /** Use the credentials manager to sign in with Google on Android */
    override suspend fun signInWithGoogle(uiContext: AuthUiContext): AuthApi.SessionStatus = try {
        logger.d(TAG) { "Starting Google sign-in process with CredentialManager" }
        require(uiContext is AndroidAuthUiContext) {
            "Google sign-in requires an AndroidAuthUiContext"
        }
        val activity = uiContext.activity

        val credentialManager = CredentialManager.create(
            context = activity,
        )

        val request: GetCredentialRequest = GetCredentialRequest.Builder().addCredentialOption(
            GetSignInWithGoogleOption.Builder(
                serverClientId = SuperDiarySecret.googleServerClientId,
            ).build(),
        ).build()

        logger.d(TAG) { "Requesting credentials from CredentialManager" }
        val result = credentialManager.getCredential(
            request = request,
            context = activity,
        )

        val googleIdToken = getGoogleIdToken(result)

        if (googleIdToken != null) {
            logger.d(TAG) { "Google ID token successfully retrieved. Attempting Supabase sign-in." }
            signInWithGoogle(googleIdToken)
        } else {
            logger.e(TAG) { "Google ID token retrieval failed. No token available for sign-in." }
            AuthApi.SessionStatus.Unauthenticated(Exception("Error logging in with Google"))
        }
    } catch (e: GetCredentialProviderConfigurationException) {
        // The dependency can be packaged correctly while the device still has no usable
        // Credential Manager provider (for example, an AOSP emulator without Play Services).
        logger.w(TAG, e) {
            "No Credential Manager provider is available on this device"
        }
        AuthApi.SessionStatus.Unauthenticated(
            NoCredentialsException("Google sign-in is not available on this device"),
        )
    } catch (e: GetCredentialException) {
        // NoCredentialException is the expected result when the device has no Google account.
        // Keep every Credential Manager operation inside this boundary so it cannot escape to
        // the ViewModel's coroutine as an uncaught exception.
        logger.e(TAG) { "Error getting credentials: ${e.message}" }
        AuthApi.SessionStatus.Unauthenticated(resolveCredentialException(e))
    } catch (e: GoogleIdTokenParsingException) {
        logger.e(TAG) { "Error parsing Google ID token: ${e.message}" }
        AuthApi.SessionStatus.Unauthenticated(e)
    }

    private fun resolveCredentialException(e: GetCredentialException): Exception = when (e) {
        is GetCredentialCancellationException -> NoCredentialsException("Authentication cancelled!")
        is NoCredentialException -> NoCredentialsException("No Google credentials found!")
        is GetCredentialInterruptedException -> NoCredentialsException("Authentication cancelled!")
        is GetCredentialUnknownException -> NoCredentialsException("An unknown error occurred!")
        else -> NoCredentialsException("An unknown error occurred!")
    }

    private fun getGoogleIdToken(result: GetCredentialResponse): String? {
        logger.d(TAG) { "Extracting Google ID token from CredentialResponse" }
        // Handle the successfully returned credential.
        return when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)

                    val googleIdToken = googleIdTokenCredential.idToken
                    googleIdToken
                } else {
                    logger.e(TAG) { "Unexpected credential type: ${credential.type}" }
                    null
                }
            }

            else -> {
                logger.e(TAG) { "Unexpected credential type: ${result.credential::class.java}" }
                null
            }
        }
    }

    companion object {
        private const val TAG = "AndroidAuth"
    }
}
