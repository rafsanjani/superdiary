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

        fun fromString(rawString: String): Location {
            if (rawString.isEmpty()) return Empty

            val (latitudeStr, longitudeStr) = rawString.split(",")
            if (latitudeStr.isEmpty() || longitudeStr.isEmpty()) {
                throw IllegalArgumentException("Invalid location string")
            }

            val latitude = latitudeStr.toDoubleOrNull()
            val longitude = longitudeStr.toDoubleOrNull()

            if (latitude == null || longitude == null) {
                throw IllegalArgumentException("Error parsing location from strings")
            }

            return Location(latitude, longitude)
        }
    }

    override fun toString(): String = "$latitude,$longitude"
}
