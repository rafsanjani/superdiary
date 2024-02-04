package me.saket.swipe

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlin.math.absoluteValue

@Composable
fun rememberSwipeableActionsState(swipeBoxSizePx: Int): SwipeableActionsState {
    return remember { SwipeableActionsState(swipeBoxSizePx) }
}

/** The state of a [SwipeableActionsBox]. */
@Stable
class SwipeableActionsState internal constructor(private val swipeBoxSizePx: Int) {
    /** The current position (in pixels) of a [SwipeableActionsBox]. */
    val offset: State<Float> get() = offsetState
    private var offsetState = mutableStateOf(0f)

    /**
     * Whether [SwipeableActionsBox] is currently animating to reset its offset
     * after it was swiped.
     */
    var isResettingOnRelease: Boolean by mutableStateOf(false)
        private set

    internal lateinit var canSwipeTowardsRight: () -> Boolean
    internal lateinit var canSwipeTowardsLeft: () -> Boolean

    internal val draggableState = DraggableState { delta ->
        var newDelta = offsetState.value + delta

        if (newDelta.absoluteValue >= swipeBoxSizePx)
            return@DraggableState

        if (!canSwipeTowardsRight()) {
            newDelta = newDelta.coerceAtMost(0f)
        }
        if (!canSwipeTowardsLeft()) {
            newDelta = newDelta.coerceAtLeast(0f)
        }
        offsetState.value = newDelta
    }

    suspend fun snapToEnd() {
        if (offset.value == 0f) return

        draggableState.drag(MutatePriority.PreventUserInput) {
            Animatable(offsetState.value).animateTo(
                targetValue = swipeBoxSizePx.toFloat() * -1,
                tween(durationMillis = animationDurationMs)
            ) {
                dragBy(value - offsetState.value)
            }
        }
    }


    internal suspend fun resetOffset() {
        draggableState.drag(MutatePriority.PreventUserInput) {
            isResettingOnRelease = true
            try {
                Animatable(offsetState.value).animateTo(targetValue = 0f, tween(durationMillis = animationDurationMs)) {
                    dragBy(value - offsetState.value)
                }
            } finally {
                isResettingOnRelease = false
            }
        }
    }
}
