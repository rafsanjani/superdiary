package com.foreverrafs.superdiary.core.logging

import io.sentry.Sentry
import io.sentry.SentryLevel

class SentryLogger : Logger {
    override fun d(tag: String, message: () -> String) {
        Sentry.captureMessage(message(), SentryLevel.DEBUG)
    }

    override fun i(tag: String, message: () -> String) {
        Sentry.captureMessage(message(), SentryLevel.INFO)
    }

    override fun e(tag: String, throwable: Throwable?, message: () -> String) {
        if (throwable != null) {
            Sentry.captureException(throwable)
        }
    }

    override fun v(tag: String, throwable: Throwable?, message: () -> String) {
        if (throwable != null) {
            Sentry.captureException(throwable)
        }
    }

    override fun w(tag: String, throwable: Throwable?, message: () -> String) {
        Sentry.captureMessage(message(), SentryLevel.WARNING)
    }
}
