package com.foreverrafs.superdiary.core.logging

class AggregateLogger(
    private val loggers: List<Logger> = emptyList(),
) {
    fun v(tag: String, throwable: Throwable? = null, message: () -> String = { "" }) {
        loggers.forEach { logger ->
            logger.v(tag = tag, throwable = throwable, message = message)
        }
    }

    fun d(tag: String, message: () -> String = { "" }) {
        loggers.forEach { logger ->
            logger.d(tag = tag, message = message)
        }
    }

    fun i(tag: String, message: () -> String = { "" }) {
        loggers.forEach { logger ->
            logger.i(tag = tag, message = message)
        }
    }

    fun e(tag: String, throwable: Throwable? = null, message: () -> String = { "" }) {
        loggers.forEach { logger ->
            logger.e(tag = tag, message = message, throwable = throwable)
        }
    }

    fun w(tag: String, throwable: Throwable? = null, message: () -> String = { "" }) {
        loggers.forEach { logger ->
            logger.w(tag = tag, message = message, throwable = throwable)
        }
    }
}
