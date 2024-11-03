package com.foreverrafs.auth.di

import com.foreverrafs.auth.AppleAuth
import com.foreverrafs.auth.AuthApi
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal actual fun platformAuthModule(): Module = module {
    singleOf(::AppleAuth) { bind<AuthApi>() }
}
