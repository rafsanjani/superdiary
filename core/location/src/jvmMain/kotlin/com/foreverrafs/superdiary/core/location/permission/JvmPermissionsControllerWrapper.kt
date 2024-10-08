package com.foreverrafs.superdiary.core.location.permission

class JvmPermissionsControllerWrapper : PermissionsControllerWrapper {
    override suspend fun providePermission(permission: Permission) {
        // TODO: Request for permission on macos
    }

    override suspend fun isPermissionGranted(permission: Permission): Boolean {
        // TODO: Check for permission state on macos
        return true
    }

    override suspend fun getPermissionState(permission: Permission): PermissionState {
        // TODO: Check for permission state on macos
        return PermissionState.Granted
    }

    override fun openAppSettings() {
        // TODO: Open app settings on macos
    }
}
