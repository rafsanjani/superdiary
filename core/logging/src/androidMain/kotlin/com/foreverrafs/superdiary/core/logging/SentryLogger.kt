package com.foreverrafs.superdiary.core.logging

import io.sentry.Sentry

// Use sentry to log errors and exceptions
class SentryLogger : Logger {
    override fun e(tag: String, throwable: Throwable?, message: () -> String) {
        if (throwable != null) {
            Sentry.captureException(throwable)
        }
    }
}
