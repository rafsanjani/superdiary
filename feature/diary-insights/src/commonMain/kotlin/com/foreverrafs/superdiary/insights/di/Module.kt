package com.foreverrafs.superdiary.insights.di

import com.foreverrafs.superdiary.insights.data.WritingInsightsRepositoryImpl
import com.foreverrafs.superdiary.insights.domain.repository.WritingInsightsRepository
import com.foreverrafs.superdiary.insights.presentation.WritingInsightsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val writingInsightsModule: Module = module {
    factoryOf(::WritingInsightsRepositoryImpl) bind WritingInsightsRepository::class
    factoryOf(::WritingInsightsViewModel)
}
