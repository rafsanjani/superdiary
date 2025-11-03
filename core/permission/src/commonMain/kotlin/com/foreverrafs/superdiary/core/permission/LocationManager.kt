package com.foreverrafs.superdiary.core.permission

interface LocationManager {
    fun requestLocation(
        onError: (Exception) -> Unit,
        onLocation: (latitude: Double, longitude: Double) -> Unit,
    )

    fun stopRequestingLocation()
}
