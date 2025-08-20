package com.foreverrafs.superdiary.core.location.permission

import com.foreverrafs.superdiary.core.logging.AggregateLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update

class LocationPermissionManager(
    private val permissionsController: PermissionsControllerWrapper,
    private val logger: AggregateLogger,
) {
    private var _permissionState = MutableStateFlow(PermissionState.NotDetermined)

    val permissionState: Flow<PermissionState> = _permissionState
        .onStart { readInitialPermissionState() }

    private suspend fun readInitialPermissionState() {
        _permissionState.update {
            val state = permissionsController.getPermissionState(LocationPermission)
            logger.i(TAG) {
                "Initial state of location permission: $state"
            }
            state
        }
    }

    suspend fun provideLocationPermission() {
        if (_permissionState.value == PermissionState.DeniedAlways ||
            _permissionState.value == PermissionState.NotGranted
        ) {
            logger.i(TAG) {
                "Location is permanently denied. Opening app settings"
            }
            permissionsController.openAppSettings()
            return
        }

        try {
            logger.i(TAG) {
                "Requesting location permission"
            }

            permissionsController.providePermission(LocationPermission)

            logger.i(TAG) {
                "Location permission granted"
            }

            _permissionState.update {
                PermissionState.Granted
            }
        } catch (_: DeniedAlwaysException) {
            logger.i(TAG) {
                "Location permission permanently denied"
            }
            _permissionState.update {
                PermissionState.DeniedAlways
            }
        } catch (_: DeniedException) {
            logger.i(TAG) {
                "Location permission denied"
            }
            _permissionState.update {
                PermissionState.Denied
            }
        }
    }

    fun getPermissionsController(): PermissionsControllerWrapper = permissionsController

    companion object {
        private val TAG = LocationPermissionManager::class.simpleName.orEmpty()
    }
}
