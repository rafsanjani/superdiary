package com.foreverrafs.superdiary.ui.di

import androidx.lifecycle.SavedStateHandle
import dev.icerock.moko.permissions.ios.PermissionsController
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual fun savedStateModule(): Module = module {
    singleOf(::PermissionsController) { bind<PermissionsController>() }
    single<SavedStateHandle> { SavedStateHandle() }
}
