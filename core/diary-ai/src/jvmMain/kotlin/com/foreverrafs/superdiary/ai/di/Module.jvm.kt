package com.foreverrafs.superdiary.ai.di

import com.foreverrafs.superdiary.ai.JvmToonEncoder
import com.foreverrafs.superdiary.ai.ToonEncoder
import org.koin.core.module.Module
import org.koin.dsl.module

actual val toonModule: Module = module {
    factory<ToonEncoder> { JvmToonEncoder() }
}
