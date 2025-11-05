package com.foreverrafs.superdiary.core.permission.di

import com.foreverrafs.superdiary.core.permission.JvmLocationManager
import com.foreverrafs.superdiary.core.permission.JvmPermissionsControllerWrapper
import com.foreverrafs.superdiary.core.permission.LocationManager
import com.foreverrafs.superdiary.core.permission.LocationPermissionManager
import com.foreverrafs.superdiary.core.permission.PermissionsControllerWrapper
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual fun permissionsModule(): Module = module {
    factoryOf(::JvmPermissionsControllerWrapper) { bind<PermissionsControllerWrapper>() }
    factoryOf(::JvmLocationManager) { bind<LocationManager>() }
    factoryOf(::LocationPermissionManager)
}
