package com.foreverrafs.superdiary.core.location.permission

/**
 * Permission Controller Wrapper. This makes it possible to build and use
 * the app on JVM because moko-permissions doesn't provide a JVM target
 *
 * @see
 *    https://github.com/icerockdev/moko-permissions/blob/master/permissions/src/commonMain/kotlin/dev/icerock/moko/permissions/PermissionsController.kt
 */
interface PermissionsControllerWrapper {
    suspend fun providePermission(permission: Permission)
    suspend fun isPermissionGranted(permission: Permission): Boolean
    suspend fun getPermissionState(permission: Permission): PermissionState
    fun openAppSettings()
}
