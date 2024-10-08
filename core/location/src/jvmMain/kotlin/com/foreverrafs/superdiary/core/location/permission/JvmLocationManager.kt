package com.foreverrafs.superdiary.core.location.permission

import com.foreverrafs.superdiary.core.location.Location
import com.foreverrafs.superdiary.core.location.LocationManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class JvmLocationManager : LocationManager {
    override fun requestLocation(): Flow<Location> = emptyFlow()

    override fun stopRequestingLocation() = Unit
}
