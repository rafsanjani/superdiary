package com.foreverrafs.superdiary.dashboard.di

import com.foreverrafs.superdiary.dashboard.DashboardViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val dashboardModule: Module = module {
    viewModelOf(::DashboardViewModel)
}
