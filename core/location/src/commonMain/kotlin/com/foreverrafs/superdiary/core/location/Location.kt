package com.foreverrafs.superdiary.core.location

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val latitude: Double,
    val longitude: Double,
) {
    fun isEmpty(): Boolean = latitude == 0.0 && longitude == 0.0

    companion object {
        val Empty: Location = Location(
            latitude = 0.0,
            longitude = 0.0,
        )
    }
}
