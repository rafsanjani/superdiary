package com.foreverrafs.superdiary.ui.di

import androidx.lifecycle.SavedStateHandle
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun permissionModule(): Module = module {
    single<SavedStateHandle> {
        SavedStateHandle()
    }
}
