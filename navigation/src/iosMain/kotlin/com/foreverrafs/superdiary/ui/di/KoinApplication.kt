package com.foreverrafs.superdiary.ui.di

import com.foreverrafs.auth.BiometricAuth
import com.foreverrafs.auth.GoogleTokenProvider
import com.foreverrafs.superdiary.ai.ToonEncoder
import com.foreverrafs.superdiary.core.analytics.AnalyticsTracker
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

@Suppress("unused")
object KoinApplication {
    fun initialize(
        analytics: AnalyticsTracker,
        logger: AggregateLogger,
        googleTokenProvider: GoogleTokenProvider,
        biometricAuth: BiometricAuth,
        toonEncoder: ToonEncoder,
    ) = startKoin {
        val tokenModule = module {
            factory<GoogleTokenProvider> { googleTokenProvider }
        }

        val biometricAuthModule = module {
            factory<BiometricAuth> { biometricAuth }
        }

        val toonModule = module {
            factory<ToonEncoder> { toonEncoder }
        }

        modules(
            modules = compositeModule(
                analytics = analytics,
                logger = logger,
            )
                .plus(tokenModule)
                .plus(toonModule)
                .plus(biometricAuthModule),
        )
    }
}
