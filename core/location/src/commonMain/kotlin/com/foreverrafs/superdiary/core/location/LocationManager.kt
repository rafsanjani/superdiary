package com.foreverrafs.superdiary.core.location

import kotlinx.coroutines.flow.Flow

class Location(
    val latitude: Double,
    val longitude: Double,
)

interface LocationManager {
    fun requestLocation(): Flow<Location>
    fun stopRequestingLocation()
}
