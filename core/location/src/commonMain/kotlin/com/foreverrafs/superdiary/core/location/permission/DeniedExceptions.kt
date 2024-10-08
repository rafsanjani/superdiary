package com.foreverrafs.superdiary.core.location.permission

expect open class DeniedException : Throwable

expect class DeniedAlwaysException : DeniedException
