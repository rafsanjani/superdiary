package com.foreverrafs.superdiary.core.location

interface LocationManager {
    fun requestLocation(
        onError: (Exception) -> Unit,
        onLocation: (Location) -> Unit
    )

    fun stopRequestingLocation()
}
