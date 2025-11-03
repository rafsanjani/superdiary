package com.foreverrafs.superdiary.creatediary

import com.foreverrafs.superdiary.core.permission.Permission
import com.foreverrafs.superdiary.core.permission.PermissionState
import com.foreverrafs.superdiary.core.permission.PermissionsControllerWrapper

class FakePermissionsControllerWrapper : PermissionsControllerWrapper {
    sealed interface ActionPerformed {
        data object ProvidePermission : ActionPerformed
        data object OpenAppSettings : ActionPerformed
    }

    var permissionStateResult: PermissionState? = null
    var actionPerformed: ActionPerformed? = null
        private set

    override suspend fun providePermission(permission: Permission) {
        actionPerformed = ActionPerformed.ProvidePermission
    }

    override suspend fun isPermissionGranted(permission: Permission): Boolean =
        permissionStateResult == PermissionState.Granted

    override suspend fun getPermissionState(permission: Permission): PermissionState {
        require(permissionStateResult != null) {
            "permissionStateResult must be initialized before calling getPermissionstate"
        }
        return permissionStateResult!!
    }

    override fun openAppSettings() {
        actionPerformed = ActionPerformed.OpenAppSettings
    }
}
