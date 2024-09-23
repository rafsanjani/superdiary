package com.foreverrafs.superdiary.core.logging

import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

@OptIn(ExperimentalObjCName::class)
@ObjCName(swiftName = "Logger")
interface Logger {
    fun v(tag: String, throwable: Throwable? = null, message: () -> String = { "" }) = Unit

    fun d(tag: String, message: () -> String = { "" }) = Unit

    fun i(tag: String, message: () -> String = { "" }) = Unit

    fun e(tag: String, throwable: Throwable? = null, message: () -> String = { "" }) = Unit

    fun w(tag: String, throwable: Throwable? = null, message: () -> String = { "" }) = Unit

    companion object : Logger
}
