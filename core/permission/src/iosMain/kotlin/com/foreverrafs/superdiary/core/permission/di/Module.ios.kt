package com.foreverrafs.superdiary.core.permission.di

import com.foreverrafs.superdiary.core.permission.AppleLocationManager
import com.foreverrafs.superdiary.core.permission.ApplePermissionsControllerWrapper
import com.foreverrafs.superdiary.core.permission.LocationManager
import com.foreverrafs.superdiary.core.permission.PermissionsControllerWrapper
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual fun permissionsModule(): Module = module {
    factoryOf(::AppleLocationManager) { bind<LocationManager>() }
    factoryOf(::ApplePermissionsControllerWrapper) { bind<PermissionsControllerWrapper>() }
}
