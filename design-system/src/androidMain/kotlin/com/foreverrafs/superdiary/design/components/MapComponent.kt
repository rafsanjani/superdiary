package com.foreverrafs.superdiary.design.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapColorScheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState

@Composable
actual fun MapComponent(
    latitude: Double,
    longitude: Double,
    modifier: Modifier,
) {
    val coordinates = LatLng(latitude, longitude)
    val markerState = rememberUpdatedMarkerState(position = coordinates)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(coordinates, 11f)
    }

    val systemInDarkTheme = isSystemInDarkTheme()

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            scrollGesturesEnabled = false,
            rotationGesturesEnabled = false,
            mapToolbarEnabled = false,
            tiltGesturesEnabled = false,
            zoomGesturesEnabled = false,
            scrollGesturesEnabledDuringRotateOrZoom = false,
            zoomControlsEnabled = false,
        ),
        googleMapOptionsFactory = {
            GoogleMapOptions().mapColorScheme(
                if (systemInDarkTheme) MapColorScheme.DARK else MapColorScheme.LIGHT,
            )
        },
    ) {
        Marker(
            state = markerState,
        )
    }
}
