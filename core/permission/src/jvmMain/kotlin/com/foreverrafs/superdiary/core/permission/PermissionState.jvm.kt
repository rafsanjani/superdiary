package com.foreverrafs.superdiary.core.permission

actual enum class PermissionState {
    NotDetermined,
    DeniedAlways,
    NotGranted,
    Granted,
    Denied,
}
