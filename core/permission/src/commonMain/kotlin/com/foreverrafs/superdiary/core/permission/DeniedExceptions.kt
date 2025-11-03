package com.foreverrafs.superdiary.core.permission

expect open class DeniedException : Throwable

expect class DeniedAlwaysException : DeniedException
