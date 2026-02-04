package com.foreverrafs.superdiary.core.permission

class JvmPermissionsControllerWrapper : PermissionsControllerWrapper {
    override suspend fun providePermission(permission: Permission) {
        // No runtime permissions on JVM; treat as granted.
    }

    override suspend fun isPermissionGranted(permission: Permission): Boolean {
        return true
    }

    override suspend fun getPermissionState(permission: Permission): PermissionState {
        return PermissionState.Granted
    }

    override fun openAppSettings() {
        // No-op on JVM; app settings are managed by the OS.
    }
}
