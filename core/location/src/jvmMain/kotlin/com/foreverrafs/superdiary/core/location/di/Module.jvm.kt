package com.foreverrafs.superdiary.core.location.di

import com.foreverrafs.superdiary.core.location.LocationManager
import com.foreverrafs.superdiary.core.location.permission.JvmLocationManager
import com.foreverrafs.superdiary.core.location.permission.JvmPermissionsControllerWrapper
import com.foreverrafs.superdiary.core.location.permission.PermissionsControllerWrapper
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual fun locationModule(): Module = module {
    factoryOf(::JvmPermissionsControllerWrapper) { bind<PermissionsControllerWrapper>() }
    factoryOf(::JvmLocationManager) { bind<LocationManager>() }
}
