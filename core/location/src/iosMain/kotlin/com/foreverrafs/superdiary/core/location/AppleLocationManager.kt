package com.foreverrafs.superdiary.core.location

import com.foreverrafs.superdiary.core.logging.AggregateLogger
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
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
    override fun requestLocation(onError: (Exception) -> Unit, onLocation: (Location) -> Unit) {
        locationManager.desiredAccuracy = kCLLocationAccuracyBest

        logger.i(TAG) {
            "Location updates started"
        }

        locationManager.delegate = object : CLLocationManagerDelegateProtocol, NSObject() {
            override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
                val location = didUpdateLocations.last() as? CLLocation
                val coordinate = location?.coordinate?.useContents { Location(latitude, longitude) }

                coordinate?.let {
                    logger.i(TAG) {
                        "Emitting new Location: [latitude=${it.latitude}, longitude=${it.longitude}]"
                    }

                    onLocation(coordinate)
                }
            }

            override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
                logger.e(TAG) {
                    didFailWithError.localizedDescription
                }
            }
        }

        locationManager.startUpdatingLocation()
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
