package com.foreverrafs.superdiary.common.coroutines

import app.cash.turbine.ReceiveTurbine

suspend inline fun <T> ReceiveTurbine<T>.awaitUntil(
    predicate: (value: T) -> Boolean,
): T {
    var value = awaitItem()

    while (!predicate(value)) {
        value = awaitItem()
    }

    return value
}
