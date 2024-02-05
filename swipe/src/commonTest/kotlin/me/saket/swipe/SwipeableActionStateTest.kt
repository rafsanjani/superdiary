package me.saket.swipe

import androidx.compose.runtime.MonotonicFrameClock
import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext

class SwipeableActionStateTest {
    private lateinit var swipeableActionState: SwipeableActionState
    private val iconWidthPx = 200
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        swipeableActionState = SwipeableActionState(iconWidthPx = iconWidthPx)
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Resetting swipe should move offset back to zero`() = runTest {
        withContext(TestClock) {
            swipeableActionState.dragBy(-100f)
            swipeableActionState.resetDrag()
            assertThat(swipeableActionState.offset.value).isEqualTo(0.0f)
        }
    }

    @Test
    fun `Swiping on an item should reveal it's action box`() = runTest {
        withContext(TestClock) {
            swipeableActionState.dragBy(-100.0f)
            assertThat(swipeableActionState.offset.value).isEqualTo(-100.0f)
        }
    }

    object TestClock : MonotonicFrameClock {
        private var time = 0L

        override suspend fun <R> withFrameNanos(onFrame: (frameTimeNanos: Long) -> R): R {
            time += 16_000_000
            return onFrame(time)
        }
    }
}
