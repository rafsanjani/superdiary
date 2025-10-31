package com.foreverrafs.superdiary.core.permission

import android.annotation.SuppressLint
import android.content.Context
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

class AndroidLocationManager(
    context: Context,
    private val logger: AggregateLogger,
) : LocationManager {

    private val locationManager = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override fun requestLocation(
        onError: (Exception) -> Unit,
        onLocation: (Double, Double) -> Unit,
    ) {
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

                onLocation(
                    location.latitude,
                    location.longitude,
                )
            }
            .addOnFailureListener { exception ->
                logger.e(tag = TAG, throwable = exception)

                onError(
                    exception,
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
