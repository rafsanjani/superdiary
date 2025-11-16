package com.foreverrafs.superdiary.ai.di

import com.foreverrafs.superdiary.ai.AndroidToonEncoder
import com.foreverrafs.superdiary.ai.ToonEncoder
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual val toonModule: Module = module {
    factoryOf(::AndroidToonEncoder) { bind<ToonEncoder>() }
}
