package com.foreverrafs.superdiary.di

import com.foreverrafs.auth.AuthApi
import com.foreverrafs.superdiary.ai.api.DiaryAI
import com.foreverrafs.superdiary.data.datasource.remote.DiaryApi
import com.foreverrafs.superdiary.fakes.FakeAndroidAuth
import com.foreverrafs.superdiary.fakes.FakeDiaryAI
import com.foreverrafs.superdiary.fakes.FakeDiaryApi
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val demoModule: Module = module {
    factoryOf(::FakeAndroidAuth) { bind<AuthApi>() }
    factoryOf(::FakeDiaryApi) { bind<DiaryApi>() }
    factoryOf(::FakeDiaryAI) { bind<DiaryAI>() }
}
