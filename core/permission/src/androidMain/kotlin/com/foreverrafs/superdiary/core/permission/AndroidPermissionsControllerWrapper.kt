package com.foreverrafs.superdiary.core.permission

import android.content.Context
import androidx.activity.ComponentActivity
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.PermissionsControllerImpl

class AndroidPermissionsControllerWrapper(context: Context) :
    PermissionsControllerWrapper,
    Bindable {
    private val permissionsController: PermissionsController = PermissionsControllerImpl(context)

    override suspend fun providePermission(permission: Permission) =
        permissionsController.providePermission(permission)

    override suspend fun isPermissionGranted(permission: Permission): Boolean =
        permissionsController.isPermissionGranted(permission)

    override suspend fun getPermissionState(permission: Permission): PermissionState =
        permissionsController.getPermissionState(permission)

    override fun bind(activity: ComponentActivity) {
        permissionsController.bind(activity)
    }

    override fun openAppSettings() = permissionsController.openAppSettings()
}
