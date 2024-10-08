package com.foreverrafs.superdiary.core.location.di

import com.foreverrafs.superdiary.core.location.AndroidLocationManager
import com.foreverrafs.superdiary.core.location.LocationManager
import com.foreverrafs.superdiary.core.location.permission.AndroidPermissionsControllerWrapper
import com.foreverrafs.superdiary.core.location.permission.PermissionsControllerWrapper
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual fun locationModule(): Module = module {
    factoryOf(::AndroidPermissionsControllerWrapper) { bind<PermissionsControllerWrapper>() }
    factoryOf(::AndroidLocationManager) { bind<LocationManager>() }
}
