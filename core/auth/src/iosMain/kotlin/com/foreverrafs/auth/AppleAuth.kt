package com.foreverrafs.auth

import io.github.jan.supabase.SupabaseClient

class AppleAuth(private val supabaseClient: SupabaseClient) : AuthApi by DefaultSupabaseAuth(supabaseClient)
