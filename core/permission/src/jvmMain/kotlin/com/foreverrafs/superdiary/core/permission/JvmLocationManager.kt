package com.foreverrafs.superdiary.core.permission

class JvmLocationManager : LocationManager {
    override fun requestLocation(onError: (Exception) -> Unit, onLocation: (Location) -> Unit) =
        Unit

    override fun stopRequestingLocation() = Unit
}
