package com.foreverrafs.superdiary.core.location.permission

import android.content.Context
import androidx.activity.ComponentActivity
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.PermissionsControllerImpl
import dev.icerock.moko.permissions.location.LOCATION

class AndroidPermissionsControllerWrapper(context: Context) :
    PermissionsControllerWrapper,
    Bindable {
    private val permissionsController: PermissionsController = PermissionsControllerImpl(context)

    override suspend fun providePermission(permission: Permission) =
        permissionsController.providePermission(Permission.LOCATION)

    override suspend fun isPermissionGranted(permission: Permission): Boolean =
        permissionsController.isPermissionGranted(permission)

    override suspend fun getPermissionState(permission: Permission): PermissionState =
        permissionsController.getPermissionState(permission)

    override fun bind(activity: ComponentActivity) {
        permissionsController.bind(activity)
        Permission.LOCATION
    }

    override fun openAppSettings() = permissionsController.openAppSettings()
}
