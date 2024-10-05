package com.foreverrafs.superdiary.core.location

import android.annotation.SuppressLint
import android.content.Context
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class AndroidLocationManager(
    context: Context,
    private val logger: AggregateLogger,
) : LocationManager {

    private val locationManager = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override fun requestLocation(): Flow<Location> = channelFlow {
        logger.i(TAG) {
            "Location updates started"
        }

        locationManager
            .getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token,
            )
            .addOnSuccessListener { location ->
                logger.i(TAG) {
                    "Emitting new Location: $location"
                }

                trySend(
                    Location(
                        latitude = location.latitude,
                        longitude = location.longitude,
                    ),
                )
            }
            .addOnFailureListener { exception ->
                logger.e(tag = TAG, throwable = exception)

                cancel(
                    cause = exception,
                    message = exception.message ?: "Error getting location",
                )
            }
    }

    override fun stopRequestingLocation() {
        // No operation on Android
    }

    companion object {
        private const val TAG = "AndroidLocationManager"
    }
}
