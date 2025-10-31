package com.foreverrafs.superdiary.core.permission

actual open class DeniedException : Throwable()
actual class DeniedAlwaysException : DeniedException()
