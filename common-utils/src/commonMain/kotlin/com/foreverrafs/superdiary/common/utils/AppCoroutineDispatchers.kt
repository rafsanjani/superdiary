package com.foreverrafs.superdiary.common.utils

import kotlinx.coroutines.CoroutineDispatcher

interface AppCoroutineDispatchers {
    val io: CoroutineDispatcher
    val computation: CoroutineDispatcher
    val main: CoroutineDispatcher
}
