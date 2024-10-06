package com.foreverrafs.superdiary.ui.di

import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.PermissionsControllerImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual fun permissionModule(): Module = module {
    singleOf(::PermissionsControllerImpl) { bind<PermissionsController>() }
}
