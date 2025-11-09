package com.foreverrafs.superdiary.ui.di

import androidx.lifecycle.SavedStateHandle
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun savedStateModule(): Module = module {
    single<SavedStateHandle> {
        SavedStateHandle()
    }
}
