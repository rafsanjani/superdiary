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

/**
 * Only used on Android. Allows the permission controller to bind to the
 * instance of the underlying activity housing the screen
 */
fun interface Bindable {
    fun bind(activityWrapper: ActivityWrapper)
}
