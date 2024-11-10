package com.foreverrafs.auth.di

import com.foreverrafs.auth.AuthApi
import com.foreverrafs.auth.DefaultSupabaseAuth
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual fun platformAuthModule(): Module = module {
    singleOf(::DefaultSupabaseAuth) { bind<AuthApi>() }
}
