package com.foreverrafs.auth

import android.app.Activity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
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
    private val contextProvider: AndroidContextProvider,
) : AuthApi by DefaultSupabaseAuth(supabaseClient, logger) {

    /** Use the credentials manager to sign in with Google on Android */
    override suspend fun signInWithGoogle(): AuthApi.SignInStatus {
        logger.d(TAG) { "Starting Google sign-in process with CredentialManager" }
        // This must be an activity context
        val hostActivityContext = contextProvider.getContext()

        require(hostActivityContext is Activity) {
            "The host context must be an Activity!"
        }

        val credentialManager = CredentialManager.create(
            context = hostActivityContext,
        )

        val request: GetCredentialRequest = GetCredentialRequest
            .Builder()
            .addCredentialOption(
                GetSignInWithGoogleOption.Builder(
                    serverClientId = SuperDiarySecret.googleServerClientId,
                ).build(),
            )
            .build()

        val googleIdToken = try {
            logger.d(TAG) { "Requesting credentials from CredentialManager" }
            val result = credentialManager.getCredential(
                request = request,
                context = hostActivityContext,
            )

            getGoogleIdToken(result)
        } catch (e: GetCredentialException) {
            return if (e is NoCredentialException) {
                logger.e(TAG) { "No Google credentials available for sign-in." }
                AuthApi.SignInStatus.Error(
                    NoCredentialsException("No Google credentials available!"),
                )
            } else {
                return AuthApi.SignInStatus.Error(e)
            }
        } catch (e: GoogleIdTokenParsingException) {
            logger.e(TAG) { "Error parsing Google ID token: ${e.message}" }
            return AuthApi.SignInStatus.Error(e)
        }

        return if (googleIdToken != null) {
            logger.d(TAG) { "Google ID token successfully retrieved. Attempting Supabase sign-in." }
            signInWithGoogle(googleIdToken)
        } else {
            logger.e(TAG) { "Google ID token retrieval failed. No token available for sign-in." }
            AuthApi.SignInStatus.Error(Exception("Error logging in with Google"))
        }
    }

    private fun getGoogleIdToken(result: GetCredentialResponse): String? {
        logger.d(TAG) { "Extracting Google ID token from CredentialResponse" }
        // Handle the successfully returned credential.
        return when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential
                        .createFrom(credential.data)

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
