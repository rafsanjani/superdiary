package com.foreverrafs.auth.di

import android.content.Context
import com.foreverrafs.auth.AndroidAuth
import com.foreverrafs.auth.AuthApi
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual fun platformAuthModule(): Module = module {
    // Can't use androidContext() here because this has to be an Activity Context
    factory<AuthApi> { (context: Context) ->
        AndroidAuth(
            supabaseClient = get(),
            context = context,
            logger = get(),
        )
    }
}
