package com.foreverrafs.superdiary

import com.foreverrafs.superdiary.core.utils.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher

object TestAppDispatchers : AppCoroutineDispatchers {
    override val computation: CoroutineDispatcher
        get() = StandardTestDispatcher()
    override val io: CoroutineDispatcher
        get() = StandardTestDispatcher()

    override val main: CoroutineDispatcher
        get() = StandardTestDispatcher()
}
