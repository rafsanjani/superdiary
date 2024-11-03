package com.foreverrafs.auth.di

import com.foreverrafs.auth.AndroidAuth
import com.foreverrafs.auth.AuthApi
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual fun platformAuthModule(): Module = module {
    factory<AuthApi> {
        AndroidAuth(
            supabaseClient = get(),
            logger = get(),
        )
    }
}
