package com.foreverrafs.superdiary.core.permission.di

import com.foreverrafs.superdiary.core.permission.AndroidLocationManager
import com.foreverrafs.superdiary.core.permission.AndroidPermissionsControllerWrapper
import com.foreverrafs.superdiary.core.permission.LocationManager
import com.foreverrafs.superdiary.core.permission.PermissionsControllerWrapper
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual fun permissionsModule(): Module = module {
    factoryOf(::AndroidPermissionsControllerWrapper) { bind<PermissionsControllerWrapper>() }
    factoryOf(::AndroidLocationManager) { bind<LocationManager>() }
}
