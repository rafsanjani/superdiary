package com.foreverrafs.superdiary.chat.di

import com.foreverrafs.superdiary.chat.data.DiaryChatRepositoryImpl
import com.foreverrafs.superdiary.chat.domain.repository.DiaryChatRepository
import com.foreverrafs.superdiary.chat.presentation.DiaryChatViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val diaryChatModule: Module = module {
    factoryOf(::DiaryChatRepositoryImpl) bind DiaryChatRepository::class
    factoryOf(::DiaryChatViewModel)
}
