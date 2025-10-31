package com.foreverrafs.superdiary.core.permission

import dev.icerock.moko.permissions.ios.PermissionsController

class ApplePermissionsControllerWrapper : PermissionsControllerWrapper {
    private val permissionsController = PermissionsController()

    override suspend fun providePermission(permission: Permission) =
        permissionsController.providePermission(permission)

    override fun openAppSettings() = permissionsController.openAppSettings()

    override suspend fun getPermissionState(permission: Permission): PermissionState =
        permissionsController.getPermissionState(permission)

    override suspend fun isPermissionGranted(permission: Permission): Boolean =
        permissionsController.isPermissionGranted(permission)
}
