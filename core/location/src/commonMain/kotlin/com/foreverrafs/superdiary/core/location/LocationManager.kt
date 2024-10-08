package com.foreverrafs.superdiary.core.location

import kotlinx.coroutines.flow.Flow

interface LocationManager {
    fun requestLocation(): Flow<Location>
    fun stopRequestingLocation()
}
