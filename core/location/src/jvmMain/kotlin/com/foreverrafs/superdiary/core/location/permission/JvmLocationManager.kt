package com.foreverrafs.superdiary.core.location.permission

import com.foreverrafs.superdiary.core.location.Location
import com.foreverrafs.superdiary.core.location.LocationManager

class JvmLocationManager : LocationManager {
    override fun requestLocation(onError: (Exception) -> Unit, onLocation: (Location) -> Unit) =
        Unit

    override fun stopRequestingLocation() = Unit
}
