package com.foreverrafs.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.foreverrafs.superdiary.core.SuperDiarySecret
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken

class AndroidAuth(
    private val supabaseClient: SupabaseClient,
    private val context: Context,
    private val logger: AggregateLogger,
) : AuthApi {

    override suspend fun signInWithGoogle(): AuthApi.SignInStatus {
        val credentialManager = CredentialManager.create(context)

        val request: GetCredentialRequest = GetCredentialRequest
            .Builder()
            .addCredentialOption(
                GetGoogleIdOption
                    .Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(SuperDiarySecret.googleServerClientId)
                    .build(),
            )
            .build()

        val googleIdToken = try {
            val result = credentialManager.getCredential(
                request = request,
                context = context,
            )

            getGoogleIdToken(result)
        } catch (e: GetCredentialException) {
            return AuthApi.SignInStatus.Error(e)
        } catch (e: GoogleIdTokenParsingException) {
            return AuthApi.SignInStatus.Error(e)
        }

        return if (googleIdToken != null) {
            supabaseClient.auth.signInWith(IDToken) {
                idToken = googleIdToken
                provider = Google
            }
            AuthApi.SignInStatus.LoggedIn
        } else {
            AuthApi.SignInStatus.Error(Exception("Error logging in with Google"))
        }
    }

    private fun getGoogleIdToken(result: GetCredentialResponse): String? {
        // Handle the successfully returned credential.
        return when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential
                        .createFrom(credential.data)

                    val googleIdToken = googleIdTokenCredential.idToken
                    logger.d(TAG) {
                        "handleSignIn: ${googleIdTokenCredential.idToken}"
                    }
                    googleIdToken
                } else {
                    logger.e(TAG) {
                        "Unexpected type of credential"
                    }
                    null
                }
            }

            else -> {
                logger.e(TAG) {
                    "Unexpected type of credential"
                }
                null
            }
        }
    }

    companion object {
        private const val TAG = "AndroidAuth"
    }
}
