package com.foreverrafs.superdiary.core

import com.foreverrafs.superdiary.secrets.BuildKonfig

object SuperDiarySecret {
    val openAIKey = BuildKonfig.OPENAI_URL
    val googleServerClientId = BuildKonfig.GOOGLE_SERVER_CLIENT_ID
    val supabaseUrl = BuildKonfig.SUPABASE_URL
    val supabaseKey = BuildKonfig.SUPABASE_KEY
}
