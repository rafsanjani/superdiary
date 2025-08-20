package com.foreverrafs.auth.di

import com.foreverrafs.auth.AndroidAuth
import com.foreverrafs.auth.AndroidBiometricAuth
import com.foreverrafs.auth.AndroidContextProvider
import com.foreverrafs.auth.AuthApi
import com.foreverrafs.auth.BiometricAuth
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal actual fun platformAuthModule(): Module = module {
    single<AndroidContextProvider> { AndroidContextProvider.getInstance() }
    factoryOf(::AndroidAuth) { bind<AuthApi>() }
    factoryOf(::AndroidBiometricAuth) { bind<BiometricAuth>() }
}
