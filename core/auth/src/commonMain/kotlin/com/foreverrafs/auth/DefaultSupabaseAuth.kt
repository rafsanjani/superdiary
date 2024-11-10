package com.foreverrafs.auth

import com.foreverrafs.superdiary.core.utils.ActivityWrapper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.exceptions.BadRequestRestException
import io.github.jan.supabase.exceptions.RestException

/**
 * Provides default implementation for all the functions in [AuthApi]
 * to adhere to interface segregation. Platform classes will implement
 * [AuthApi] by using this class as a delegate and overriding some of the
 * functions
 */
class DefaultSupabaseAuth(private val client: SupabaseClient) : AuthApi {
    override suspend fun signInWithGoogle(activityWrapper: ActivityWrapper): AuthApi.SignInStatus =
        try {
            client.auth.signInWith(provider = Google)
            AuthApi.SignInStatus.LoggedIn()
        } catch (e: RestException) {
            AuthApi.SignInStatus.Error(e)
        }

    override suspend fun signInWithGoogle(googleIdToken: String): AuthApi.SignInStatus = try {
        client.auth.signInWith(IDToken) {
            idToken = googleIdToken
            provider = Google
        }
        AuthApi.SignInStatus.LoggedIn(googleIdToken)
    } catch (e: RestException) {
        if (e is BadRequestRestException) {
            // Rewrite exception into a domain type
            AuthApi.SignInStatus.Error(TokenExpiredException(message = e.message))
        } else {
            AuthApi.SignInStatus.Error(e)
        }
    }
}
