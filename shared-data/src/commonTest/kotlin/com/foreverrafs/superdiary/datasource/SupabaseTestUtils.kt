package com.foreverrafs.superdiary.datasource

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.SupabaseClientBuilder
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.serializer.KotlinXSerializer
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import kotlinx.serialization.json.Json

fun createMockedSupabaseClient(
    supabaseUrl: String = "https://projectref.supabase.co",
    supabaseKey: String = "project-anon-key",
    configuration: SupabaseClientBuilder.() -> Unit = {},
    requestHandler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData,
): SupabaseClient = createSupabaseClient(
    supabaseUrl = supabaseUrl,
    supabaseKey = supabaseKey,
) {
    httpEngine = MockEngine {
        requestHandler(it)
    }

    install(Postgrest) {
        serializer = KotlinXSerializer(
            json = Json {
                isLenient = true
                ignoreUnknownKeys = true
                useAlternativeNames = false
            },
        )
    }
    install(Realtime)

    Logger.setLogWriters(listOf<LogWriter>())
    configuration()
}
