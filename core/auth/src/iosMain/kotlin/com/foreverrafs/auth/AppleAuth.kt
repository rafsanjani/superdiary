package com.foreverrafs.auth

import com.foreverrafs.superdiary.core.utils.ActivityWrapper
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppleAuth(private val supabaseClient: SupabaseClient) :
    AuthApi by DefaultSupabaseAuth(supabaseClient),
    KoinComponent {
    private val googleTokenProvider: GoogleTokenProvider by inject()

    override suspend fun signInWithGoogle(activityWrapper: ActivityWrapper?): AuthApi.SignInStatus = try {
        val token = withContext(Dispatchers.Main) { googleTokenProvider.getGoogleToken() }
        signInWithGoogle(token)
    } catch (e: Exception) {
        AuthApi.SignInStatus.Error(e)
    }
}
