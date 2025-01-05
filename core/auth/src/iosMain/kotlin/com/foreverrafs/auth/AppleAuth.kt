package com.foreverrafs.auth

import com.foreverrafs.superdiary.core.logging.AggregateLogger
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppleAuth(private val supabaseClient: SupabaseClient, private val logger: AggregateLogger) :
    AuthApi by DefaultSupabaseAuth(supabaseClient, logger),
    KoinComponent {
    private val googleTokenProvider: GoogleTokenProvider by inject()

    override suspend fun signInWithGoogle(): AuthApi.SignInStatus =
        try {
            logger.d(Tag) {
                "Retrieving Google token from Apple"
            }
            val token = withContext(Dispatchers.Main) { googleTokenProvider.getGoogleToken() }
            logger.d(Tag) {
                "Got Google token from apple. Attempting to sign in"
            }
            signInWithGoogle(token)
        } catch (e: Exception) {
            logger.e(tag = Tag, throwable = e)
            AuthApi.SignInStatus.Error(e)
        }

    companion object {
        private val Tag = AppleAuth::class.simpleName.orEmpty()
    }
}
