package com.foreverrafs.superdiary.core.permission

expect enum class PermissionState {
    NotDetermined,
    DeniedAlways,
    NotGranted,
    Granted,
    Denied,
}
