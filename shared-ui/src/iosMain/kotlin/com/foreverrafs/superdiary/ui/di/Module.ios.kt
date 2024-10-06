package com.foreverrafs.superdiary.ui.di

import dev.icerock.moko.permissions.ios.PermissionsController
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual fun permissionModule(): Module = module {
    singleOf(::PermissionsController) { bind<PermissionsController>() }
}
