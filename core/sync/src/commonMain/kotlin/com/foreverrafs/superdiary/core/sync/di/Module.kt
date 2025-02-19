package com.foreverrafs.superdiary.core.sync.di

import com.foreverrafs.superdiary.core.sync.DiarySynchronizer
import com.foreverrafs.superdiary.core.sync.Synchronizer
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val syncModule: Module = module {
    singleOf(::DiarySynchronizer) { bind<Synchronizer>() }
}
