package com.foreverrafs.superdiary.core.location

import com.foreverrafs.superdiary.core.logging.AggregateLogger
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.Foundation.NSError
import platform.darwin.NSObject

class AppleLocationManager(
    private val logger: AggregateLogger,
) : LocationManager {
    private val locationManager = CLLocationManager()

    @OptIn(ExperimentalForeignApi::class)
    override fun requestLocation(): Flow<Location> = channelFlow {
        locationManager.desiredAccuracy = kCLLocationAccuracyBest

        logger.i(TAG) {
            "Location updates started"
        }

        locationManager.delegate = object : CLLocationManagerDelegateProtocol, NSObject() {
            override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
                val location = didUpdateLocations.last() as? CLLocation
                val coordinate = location?.coordinate?.useContents { this }

                coordinate?.let {
                    logger.i(TAG) {
                        "Emitting new Location: [latitude=${it.latitude}, longitude=${it.longitude}]"
                    }

                    trySend(
                        Location(
                            latitude = coordinate.latitude,
                            longitude = coordinate.longitude,
                        ),
                    )
                }
            }

            override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
                logger.e(TAG) {
                    didFailWithError.localizedDescription
                }
            }
        }

        locationManager.startUpdatingLocation()
        awaitClose { }
    }

    override fun stopRequestingLocation() {
        logger.i(TAG) {
            "Location updates stopped"
        }
        locationManager.stopUpdatingLocation()
    }

    companion object {
        private const val TAG = "AppleLocationManager"
    }
}
