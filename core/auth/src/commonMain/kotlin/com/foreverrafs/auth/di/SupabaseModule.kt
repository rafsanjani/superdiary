package com.foreverrafs.auth.di

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import org.koin.dsl.module

internal fun supabaseModule() = module {
    single<SupabaseClient> {
        createSupabaseClient(
            supabaseUrl = "https://opnzyxbnwhfcaauctcqc.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im9wbnp5eGJud2hmY2FhdWN0Y3FjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MjkwNTg0ODUsImV4cCI6MjA0NDYzNDQ4NX0.vgCpuiDJQTm6xGGZZBZXRTCAWo0lytnlREhQ53pe9-k",
        ) {
            install(Auth)
            install(Postgrest)
        }
    }
}

fun authModule() = module {
    includes(
        supabaseModule(),
        platformAuthModule(),
    )
}
