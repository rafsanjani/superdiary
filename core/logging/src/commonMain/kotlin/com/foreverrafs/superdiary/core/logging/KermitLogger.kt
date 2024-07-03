package com.foreverrafs.superdiary.core.logging

class KermitLogger : Logger {
    override fun v(tag: String, throwable: Throwable?, message: () -> String) {
        co.touchlab.kermit.Logger.v(tag = tag, throwable = throwable, messageString = message())
    }

    override fun d(tag: String, message: () -> String) {
        co.touchlab.kermit.Logger.d(tag = tag, messageString = message())
    }

    override fun e(tag: String, throwable: Throwable?, message: () -> String) {
        co.touchlab.kermit.Logger.e(tag = tag, throwable = throwable, messageString = message())
    }

    override fun i(tag: String, message: () -> String) {
        co.touchlab.kermit.Logger.i(messageString = message(), tag = tag)
    }

    override fun w(tag: String, throwable: Throwable?, message: () -> String) {
        co.touchlab.kermit.Logger.w(tag, throwable, message)
    }
}
