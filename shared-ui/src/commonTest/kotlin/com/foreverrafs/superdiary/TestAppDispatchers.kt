package com.foreverrafs.superdiary

import com.foreverrafs.superdiary.core.utils.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher

object TestAppDispatchers : AppCoroutineDispatchers {
    override val computation: CoroutineDispatcher
        get() = StandardTestDispatcher()
    override val io: CoroutineDispatcher
        get() = StandardTestDispatcher()
    override val main: CoroutineDispatcher
        get() = StandardTestDispatcher()
}

@OptIn(ExperimentalCoroutinesApi::class)
object UnconfinedTestAppDispatchers : AppCoroutineDispatchers {

    private val dispatcher = UnconfinedTestDispatcher()

    override val io: CoroutineDispatcher
        get() = dispatcher
    override val computation: CoroutineDispatcher
        get() = dispatcher
    override val main: CoroutineDispatcher
        get() = dispatcher
}
