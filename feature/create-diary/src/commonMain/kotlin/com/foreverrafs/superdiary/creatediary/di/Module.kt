package com.foreverrafs.superdiary.creatediary.di

import com.foreverrafs.superdiary.creatediary.screen.CreateDiaryViewModel
import com.foreverrafs.superdiary.creatediary.usecase.AddDiaryUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val createDiaryModule: Module = module {
    viewModelOf(::CreateDiaryViewModel)
    factoryOf(::AddDiaryUseCase)
}
