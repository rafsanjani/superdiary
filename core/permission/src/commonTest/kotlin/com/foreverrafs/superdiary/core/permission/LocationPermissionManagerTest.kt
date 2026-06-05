package com.foreverrafs.superdiary.core.permission

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import kotlin.test.Test
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

class LocationPermissionManagerTest {

    @Test
    fun `should request location permission when permission is not granted`() = runTest {
        val permissionsController = FakePermissionsControllerWrapper(
            permissionState = PermissionState.NotGranted,
        )
        val manager = LocationPermissionManager(
            permissionsController = permissionsController,
            logger = AggregateLogger(),
        )

        manager.permissionState.first()
        manager.provideLocationPermission()

        assertThat(permissionsController.actionPerformed)
            .isEqualTo(FakePermissionsControllerWrapper.ActionPerformed.ProvidePermission)
    }

    @Test
    fun `should open app settings when location permission is permanently denied`() = runTest {
        val permissionsController = FakePermissionsControllerWrapper(
            permissionState = PermissionState.DeniedAlways,
        )
        val manager = LocationPermissionManager(
            permissionsController = permissionsController,
            logger = AggregateLogger(),
        )

        manager.permissionState.first()
        manager.provideLocationPermission()

        assertThat(permissionsController.actionPerformed)
            .isEqualTo(FakePermissionsControllerWrapper.ActionPerformed.OpenAppSettings)
    }
}

private class FakePermissionsControllerWrapper(
    private val permissionState: PermissionState,
) : PermissionsControllerWrapper {
    sealed interface ActionPerformed {
        data object ProvidePermission : ActionPerformed
        data object OpenAppSettings : ActionPerformed
    }

    var actionPerformed: ActionPerformed? = null
        private set

    override suspend fun providePermission(permission: Permission) {
        actionPerformed = ActionPerformed.ProvidePermission
    }

    override suspend fun isPermissionGranted(permission: Permission): Boolean =
        permissionState == PermissionState.Granted

    override suspend fun getPermissionState(permission: Permission): PermissionState = permissionState

    override fun openAppSettings() {
        actionPerformed = ActionPerformed.OpenAppSettings
    }
}
