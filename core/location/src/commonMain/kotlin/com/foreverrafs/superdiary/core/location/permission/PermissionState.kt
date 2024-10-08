package com.foreverrafs.superdiary.core.location.permission

expect enum class PermissionState {
    NotDetermined,
    DeniedAlways,
    NotGranted,
    Granted,
    Denied,
}
