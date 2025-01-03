package com.foreverrafs.superdiary.profile.di

import com.foreverrafs.superdiary.profile.data.ProfileRepositoryImpl
import com.foreverrafs.superdiary.profile.domain.repository.ProfileRepository
import com.foreverrafs.superdiary.profile.domain.usecase.GetCurrentUserUseCase
import com.foreverrafs.superdiary.profile.presentation.ProfileScreenViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val profileModule: Module = module {
    factoryOf(::ProfileRepositoryImpl) bind ProfileRepository::class
    viewModelOf(::ProfileScreenViewModel)
    factoryOf(::GetCurrentUserUseCase)
}
