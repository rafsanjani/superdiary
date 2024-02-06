package me.saket.swipe

import androidx.compose.runtime.MonotonicFrameClock
import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.math.roundToInt
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext

@OptIn(ExperimentalCoroutinesApi::class)
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
        withContext(TestMonotonicFrameClock) {
            swipeableActionState.dragBy(-100f)
            swipeableActionState.resetDrag()
            assertThat(swipeableActionState.offset.value).isEqualTo(0.0f)
        }
    }

    @Test
    fun `Swiping on an item should reveal it's action box`() = runTest {
        withContext(TestMonotonicFrameClock) {
            swipeableActionState.dragBy(-100.0f)
            assertThat(swipeableActionState.offset.value).isEqualTo(-100.0f)
        }
    }

    @Test
    fun `Finalize drag should do nothing if item is at correct position`() = runTest {
        withContext(TestMonotonicFrameClock) {
            swipeableActionState.finalizeDrag()
            assertThat(swipeableActionState.offset.value).isEqualTo(0f)
        }
    }

    @Test
    fun `Should drag to finish if item is dragged past threshold`() = runTest {
        withContext(TestMonotonicFrameClock) {
            swipeableActionState.dragBy(-(iconWidthPx / 2).toFloat())
            swipeableActionState.finalizeDrag()
            assertThat(swipeableActionState.offset.value.roundToInt()).isEqualTo(-iconWidthPx)
        }
    }

    @Test
    fun `Should reset drag when item is not dragged past threshold`() = runTest {
        withContext(TestMonotonicFrameClock) {
            swipeableActionState.dragBy(-(iconWidthPx / 3).toFloat())
            swipeableActionState.finalizeDrag()
            assertThat(swipeableActionState.offset.value.roundToInt()).isEqualTo(0)
        }
    }

    object TestMonotonicFrameClock : MonotonicFrameClock {
        private var time = 0L

        override suspend fun <R> withFrameNanos(onFrame: (frameTimeNanos: Long) -> R): R {
            time += 16_000_000
            return onFrame(time)
        }
    }
}
