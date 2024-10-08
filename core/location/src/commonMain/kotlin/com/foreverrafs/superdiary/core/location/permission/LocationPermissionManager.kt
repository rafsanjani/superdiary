package com.foreverrafs.superdiary.core.location.permission

import com.foreverrafs.superdiary.core.logging.AggregateLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LocationPermissionManager(
    private val permissionsController: PermissionsControllerWrapper,
    private val logger: AggregateLogger,
) {
    private var _permissionState = MutableStateFlow(PermissionState.NotDetermined)

    val permissionState: StateFlow<PermissionState> = _permissionState

    init {
        CoroutineScope(Dispatchers.Default).launch {
            _permissionState.update {
                val state = permissionsController.getPermissionState(Permission.LOCATION)
                logger.i(TAG) {
                    "Initial state of location permission: $state"
                }
                state
            }
        }
    }

    suspend fun provideLocationPermission() {
        if (permissionState.value == PermissionState.DeniedAlways ||
            permissionState.value == PermissionState.NotGranted
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

            permissionsController.providePermission(Permission.LOCATION)

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
