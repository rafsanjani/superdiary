package com.foreverrafs.auth

import androidx.compose.runtime.Composable
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Apple
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.appleNativeLogin
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithApple
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import io.github.jan.supabase.compose.auth.composeAuth
import io.github.jan.supabase.compose.auth.googleNativeLogin
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

class SupabaseAuthWrapper {
    private val supabaseClient = createSupabaseClient(
        supabaseUrl = "https://opnzyxbnwhfcaauctcqc.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im9wbnp5eGJud2hmY2FhdWN0Y3FjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MjkwNTg0ODUsImV4cCI6MjA0NDYzNDQ4NX0.vgCpuiDJQTm6xGGZZBZXRTCAWo0lytnlREhQ53pe9-k",
    ) {

        install(Auth)
        install(Postgrest)
        install(ComposeAuth) {
            appleNativeLogin()
            googleNativeLogin(
                serverClientId = "867773535981-mucnrga2bj02k93u811mopubf9hcp0tc.apps.googleusercontent.com",
            )
        }
    }

    // This is a leaky abstraction but we'll leave it for now
    @Composable
    fun rememberSignInWithGoogle(
        onResult: (NativeSignInResult) -> Unit,
    ) = supabaseClient.composeAuth.rememberSignInWithGoogle(
        onResult = onResult,
    )

    // This is a leaky abstraction but we'll leave it for now
    @Composable
    fun rememberSignInWithApple(
        onResult: (NativeSignInResult) -> Unit,
    ) = supabaseClient.composeAuth.rememberSignInWithApple(
        onResult = onResult,
    )

    suspend fun signInWithApple() = supabaseClient.auth.signInWith(Apple)
}
