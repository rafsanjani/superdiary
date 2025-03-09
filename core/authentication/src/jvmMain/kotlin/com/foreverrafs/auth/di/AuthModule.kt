package com.foreverrafs.auth.di

import com.foreverrafs.auth.AuthApi
import com.foreverrafs.auth.BiometricAuth
import com.foreverrafs.auth.DefaultSupabaseAuth
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual fun platformAuthModule(): Module = module {
    factoryOf(::DefaultSupabaseAuth) { bind<AuthApi>() }
    factoryOf(::DesktopBiometricAuth) { bind<BiometricAuth>() }
}
