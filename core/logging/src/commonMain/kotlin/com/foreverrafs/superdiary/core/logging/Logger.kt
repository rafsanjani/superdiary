package com.foreverrafs.superdiary.core.logging

interface Logger {
    fun v(tag: String, throwable: Throwable? = null, message: () -> String = { "" }) = Unit

    fun d(tag: String, message: () -> String = { "" }) = Unit

    fun i(tag: String, message: () -> String = { "" }) = Unit

    fun e(tag: String, throwable: Throwable? = null, message: () -> String = { "" }) = Unit

    fun w(tag: String, message: () -> String = { "" }) = Unit

    companion object : Logger
}
