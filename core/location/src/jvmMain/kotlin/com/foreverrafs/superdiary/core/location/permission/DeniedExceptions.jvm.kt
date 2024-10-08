package com.foreverrafs.superdiary.core.location.permission

actual open class DeniedException : Throwable()
actual class DeniedAlwaysException : DeniedException()
