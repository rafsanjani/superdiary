package com.foreverrafs.superdiary.common.utils.di

import com.foreverrafs.superdiary.common.utils.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.Module
import org.koin.dsl.module

val utilsModule: Module = module {
    factory<AppCoroutineDispatchers> {
        object : AppCoroutineDispatchers {
            override val io: CoroutineDispatcher
                get() = Dispatchers.IO
            override val computation: CoroutineDispatcher
                get() = Dispatchers.Default
            override val main: CoroutineDispatcher
                get() = Dispatchers.Main
        }
    }
}
