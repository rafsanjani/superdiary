package com.foreverrafs.superdiary.database.model

import app.cash.sqldelight.ColumnAdapter

data class LocationDb(
    val latitude: Double,
    val longitude: Double,
) {
    companion object {
        fun fromString(rawString: String): LocationDb {
            val (latitudeStr, longitudeStr) = rawString.split(",")
            if (latitudeStr.isEmpty() || longitudeStr.isEmpty()) {
                throw IllegalArgumentException("Invalid location string")
            }

            val latitude = latitudeStr.toDoubleOrNull()
            val longitude = longitudeStr.toDoubleOrNull()

            if (latitude == null || longitude == null) {
                throw IllegalArgumentException("Error parsing location from strings")
            }

            return LocationDb(latitude, longitude)
        }
    }

    override fun toString(): String = "$latitude,$longitude"
}

val locationAdapter = object : ColumnAdapter<LocationDb, String> {
    override fun decode(databaseValue: String): LocationDb = LocationDb.fromString(databaseValue)

    override fun encode(value: LocationDb): String = value.toString()
}
